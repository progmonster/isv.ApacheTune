package com.apachetune.core.errorreportsystem;

import com.apachetune.core.AppManager;
import com.apachetune.core.AppVersion;
import com.apachetune.core.impl.AppManagerImpl;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.impl.PreferencesManagerImpl;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.apachetune.core.Constants.REMOTE_SERVICE_USER_EMAIL_PROP_NAME;
import static com.apachetune.core.Constants.VELOCITY_LOG4J_APPENDER_NAME;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static java.text.MessageFormat.format;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.apache.commons.httpclient.params.HttpMethodParams.RETRY_HANDLER;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS;

/**
 * FIXDOC
 */
public class ErrorReportManager {
    private static final Logger logger = LoggerFactory.getLogger(ErrorReportManager.class);

    private static final ErrorReportManager INSTANCE = new ErrorReportManager();

    public static ErrorReportManager getInstance() {
        return INSTANCE;
    }

    public final void sendErrorReport(Component parentComponent, final String message, final Throwable cause,
                                      final AppManager nullableAppManager,
                                      final PreferencesManager nullablePreferencesManager) {
        logger.info(format("Sending error info to remote service... [message={0};\ncause=\n{1}\n]",
                message, getFullStackTrace(cause)));

        try {
            final Managers managers = prepareManagers(nullableAppManager, nullablePreferencesManager);

            String lastUsedNullableUserEmail = getUserEmail(managers.getAppManager(), managers.getPreferencesManager());

            final String nullableUserEmail = showAskForReporterEmailDialog(parentComponent, lastUsedNullableUserEmail);

            final ErrorReportDialog errorReportDialog = new ErrorReportDialog(parentComponent);

            final ExecutorService executorService = newSingleThreadExecutor();

            executorService.execute(new Runnable() {
                @Override
                public final void run() {
                    try {
                        invokeLater(new Runnable() {
                            @Override
                            public final void run() {
                                try {
                                    errorReportDialog.setVisible(true);
                                } catch (Throwable throwable) {
                                    //noinspection ThrowableInstanceNeverThrown
                                    handleErrorReportingException(new ErrorReportManagerException(throwable));
                                }
                            }
                        });

                        doSendErrorReport(nullableUserEmail, message, cause, managers.getAppManager(),
                                managers.getPreferencesManager());
                    } catch (ErrorReportManagerException e) {
                        handleErrorReportingException(e);
                    } catch (Throwable throwable) {
                        //noinspection ThrowableInstanceNeverThrown
                        handleErrorReportingException(new ErrorReportManagerException(throwable));
                    } finally {
                        invokeLater(new Runnable() {
                            @Override
                            public final void run() {
                                try {
                                    errorReportDialog.dispose();
                                } catch (Throwable throwable) {
                                    //noinspection ThrowableInstanceNeverThrown
                                    handleErrorReportingException(new ErrorReportManagerException(throwable));
                                }
                            }
                        });

                        executorService.shutdown();
                    }
                }
            });
        } catch (Throwable throwable) {
            //noinspection ThrowableInstanceNeverThrown
            handleErrorReportingException(new ErrorReportManagerException(throwable));
        }
    }

    public final String getUserEmail(AppManager nullableAppManager, PreferencesManager nullablePreferencesManager) {
        Managers managers = prepareManagers(nullableAppManager, nullablePreferencesManager);

        if (managers.getPreferencesManager() == null) {
            logger.warn("Cannot get userEmail because cannot create preferences manager.");

            return null;
        }

        Preferences prefs = managers.getPreferencesManager().userNodeForPackage(ErrorReportManager.class);

        return prefs.get(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, null);
    }

    public final void storeUserEMail(String userEMail, AppManager nullableAppManager,
                                     PreferencesManager nullablePreferencesManager) {
        Managers managers = prepareManagers(nullableAppManager, nullablePreferencesManager);

        if (managers.getPreferencesManager() == null) {
            logger.warn(format("Cannot store userEmail because cannot create preferences manager [userEmail={0}]",
                    userEMail));

            return;
        }

        Preferences prefs = managers.getPreferencesManager().userNodeForPackage(ErrorReportManager.class);

        prefs.put(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, userEMail);
    }

    private String showAskForReporterEmailDialog(Component parentComponent, String defUserEmail) {
        String result = showInputDialog(parentComponent, "Please input your email to field below.\n" +
                "It may be useful for us when we was trying to solve error you got.\n" +
                "We'll no use with email to spam or ad messages and you can leave this field empty.",
                defUserEmail);

        return !result.trim().isEmpty() ? result.trim() : null;
    }

    private void doSendErrorReport(String nullableUserEmail, String message, Throwable cause,
                                   AppManager nullableAppManager, PreferencesManager nullablePreferencesManager)
            throws ErrorReportManagerException {
        final Managers managers = prepareManagers(nullableAppManager, nullablePreferencesManager);

        UUID nullableAppInstallationUid =
                (managers.getAppManager() != null) ? managers.getAppManager().getAppInstallationUid() : null;

        String nullableAppFullName =
                (managers.getAppManager() != null) ? managers.getAppManager().getFullAppName() : null;

        String errorMessageReport =
                prepareErrorMessageReport(nullableUserEmail, nullableAppFullName, nullableAppInstallationUid, message,
                        cause);

        initializeVelocity();

        postMessageToRemoteService(errorMessageReport);

        sendAppLogs(nullableUserEmail, nullableAppInstallationUid);
    }

    private void sendAppLogs(String nullableUserEmail, UUID nullableAppInstallationUid) {
        // в цикле паковать, отправлять и удалять (кроме самого последнего) файлы лога. Если на любом этапе возникнет
        // ошибка - все же продолжать работу, логгировать ее. В конце, если были ошибки, попросить пользователя отправить
        // лог вручную
        // TODO app logs and delete it
        // todo send and delete velocity log
        // TODO implement
    }

    private void initializeVelocity() {
        Velocity.setProperty(RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        Velocity.setProperty("runtime.log.logsystem.log4j.logger", "velocity_logger");

        try {
            Velocity.init();
        } catch (Exception e) {
            throw createRuntimeException(e);
        }
    }

    private void handleErrorReportingException(ErrorReportManagerException e) {
        System.out.println(getFullStackTrace(e));
        // TODO implement
    }

    private Managers prepareManagers(AppManager nullableAppManager, PreferencesManager nullablePreferencesManager) {
        PreferencesManager preferencesManager = null;

        AppManager appManager = null;

        try {
            preferencesManager =
                    (nullablePreferencesManager != null) ? nullablePreferencesManager : new PreferencesManagerProxy();

            appManager =
                    (nullableAppManager != null) ? nullableAppManager : new AppManagerProxy(preferencesManager);

            if (preferencesManager instanceof PreferencesManagerProxy) {
                PreferencesManagerProxy preferencesManagerProxy = (PreferencesManagerProxy) preferencesManager;

                preferencesManagerProxy.setAppManager(appManager);
            }
        } catch (Throwable cause) {
            logger.error("Error creating managers.");
        }

        return new Managers(appManager, preferencesManager);
    }

    private String prepareErrorMessageReport(String nullableUserEmail, String nullableAppFullName,
                                             UUID nullableAppInstallationUid, String errorMessage, Throwable cause) {
        VelocityContext ctx = new VelocityContext();

        ctx.put("appFullName", nullableAppFullName);
        ctx.put("appInstallationUid", nullableAppInstallationUid);
        ctx.put("userEMail", nullableUserEmail);

        String stackTrace = errorMessage + '\n' + getFullStackTrace(cause);

        try {
            ctx.put("base64EncodedStackTrace", encodeBase64String(stackTrace.getBytes("UTF-8")).trim());
        } catch (UnsupportedEncodingException e) {
            throw createRuntimeException(e);
        }

        Reader reader;

        try {
            reader = new InputStreamReader(getClass().getResourceAsStream("send_error_info_request.xml.vm"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw createRuntimeException(e);
        }

        StringWriter writer = new StringWriter();

        try {
            boolean isOk = Velocity.evaluate(ctx, writer, VELOCITY_LOG4J_APPENDER_NAME, reader);

            isTrue(isOk);

            writer.close();

            return writer.toString();
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    private void postMessageToRemoteService(String content) throws ErrorReportManagerException {
        HttpClient client = new HttpClient();

        PostMethod method = new PostMethod("http://apachetune.com/services/reports");

        method.getParams().setParameter(RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        method.setQueryString("action=send-error-info");

        method.setRequestHeader(new Header("Content-Type", "text/xml; charset=UTF-8"));

        try {
            method.setRequestEntity(new ByteArrayRequestEntity(content.getBytes("UTF-8"), "text/html"));
        } catch (UnsupportedEncodingException e) {
            throw createRuntimeException(e);
        }

        int resultCode;

        try {
            resultCode = client.executeMethod(method);

            if (resultCode != SC_OK) {
                throw new ErrorReportManagerException(
                        "Remote service returned non successful result [resultCode=" + resultCode + ']');
            }
        } catch (IOException e) {
            throw new ErrorReportManagerException(e);
        }

        method.releaseConnection();
    }

    class Managers {
        private final AppManager appManager;

        private final PreferencesManager preferencesManager;

        public Managers(AppManager appManager, PreferencesManager preferencesManager) {
            this.appManager = appManager;
            this.preferencesManager = preferencesManager;
        }

        public final AppManager getAppManager() {
            return appManager;
        }

        public final PreferencesManager getPreferencesManager() {
            return preferencesManager;
        }

    }

    class AppManagerProxy implements AppManager {
        private final PreferencesManager preferencesManager;

        private AppManager appManager;

        public AppManagerProxy(PreferencesManager preferencesManager) {
            notNull(preferencesManager);

            this.preferencesManager = preferencesManager;
        }

        @Override
        public final UUID getAppInstallationUid() {
            return getAppManager().getAppInstallationUid();
        }

        @Override
        public final String getName() {
            return getAppManager().getName();
        }

        @Override
        public final AppVersion getVersion() {
            return getAppManager().getVersion();
        }

        @Override
        public final Date getBuildDate() {
            return getAppManager().getBuildDate();
        }

        @Override
        public final Date getDevelopmentStartDate() {
            return getAppManager().getDevelopmentStartDate();
        }

        @Override
        public final String getVendor() {
            return getAppManager().getVendor();
        }

        @Override
        public final URL getWebSite() {
            return getAppManager().getWebSite();
        }

        @Override
        public final String getCopyrightText() {
            return getAppManager().getCopyrightText();
        }

        @Override
        public final String getFullAppName() {
            return getAppManager().getFullAppName();
        }

        @Override
        public final String getProductWebPortalUri() {
            return getAppManager().getProductWebPortalUri();
        }

        private AppManager getAppManager() {
            notNull(preferencesManager);

            if (appManager == null) {
                appManager = new AppManagerImpl(preferencesManager);
            }

            return appManager;
        }

    }

    class PreferencesManagerProxy implements PreferencesManager {
        private AppManager appManager;

        private PreferencesManager preferencesManager;

        public final void setAppManager(AppManager appManager) {
            notNull(appManager);

            this.appManager = appManager;
        }

        @Override
        public final Preferences systemNodeForPackage(Class<?> clazz) {
            return getPreferencesManager().systemNodeForPackage(clazz);
        }

        @Override
        public final Preferences systemRoot() {
            return getPreferencesManager().systemRoot();
        }

        @Override
        public final Preferences userNodeForPackage(Class<?> clazz) {
            return getPreferencesManager().userNodeForPackage(clazz);
        }

        @Override
        public final Preferences userRoot() {
            return getPreferencesManager().userRoot();
        }

        private PreferencesManager getPreferencesManager() {
            notNull(appManager);

            if (preferencesManager == null) {
                preferencesManager = new PreferencesManagerImpl(appManager);
            }

            return preferencesManager;
        }

    }

    public static void main(String[] args) {
        ErrorReportManager.getInstance().sendErrorReport(null, "error", new RuntimeException("bla-bla"), null, null);
    }
}
