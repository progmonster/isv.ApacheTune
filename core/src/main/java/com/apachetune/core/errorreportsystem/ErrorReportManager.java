package com.apachetune.core.errorreportsystem;

import com.apachetune.core.AppManager;
import com.apachetune.core.AppVersion;
import com.apachetune.core.impl.AppManagerImpl;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.impl.PreferencesManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.apachetune.core.Constants.REMOTE_SERVICE_USER_EMAIL_PROP_NAME;
import static java.text.MessageFormat.format;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.lang.Validate.notNull;

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
        final ErrorReportDialog errorReportDialog = new ErrorReportDialog(parentComponent);

        final ExecutorService executorService = newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public final void run() {
                invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        errorReportDialog.setVisible(true);
                    }
                });

                doSendErrorReport(message, cause, nullableAppManager, nullablePreferencesManager);

                invokeLater(new Runnable() {
                    @Override
                    public final void run() {
                        errorReportDialog.dispose();
                    }
                });

                executorService.shutdown();
            }
        });
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

    private void doSendErrorReport(String message, Throwable cause, AppManager nullableAppManager,
                                   PreferencesManager nullablePreferencesManager) {
        Managers managers = prepareManagers(nullableAppManager, nullablePreferencesManager);

        String nullableUserEmail = getUserEmail(managers.getAppManager(), managers.getPreferencesManager());

        UUID nullableAppInstallationUid =
                (managers.getAppManager() != null) ? managers.getAppManager().getAppInstallationUid() : null;

        System.out.println(nullableUserEmail); // todo remove

        System.out.println(nullableAppInstallationUid); // todo remove
        
        // TODO send error info and app log and delete it
        // todo если возникнет ошибка, залоггировать и  попытаться все же отправить логи, после этого попросить отправить логи вручную

        sendAppLog(nullableUserEmail, nullableAppInstallationUid);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void sendAppLog(String nullableUserEmail, UUID nullableAppInstallationUid) {
        // в цикле паковать, отправлять и удалять (кроме самого последнего) файлы лога. Если на любом этапе возникнет
        // ошибка - все же продолжать работу, логгировать ее. В конце, если были ошибки, попросить пользователя отправить
        // лог вручную
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
        ErrorReportManager.getInstance().sendErrorReport(null, "Test error", new RuntimeException("Heya!"), null, null);
    }
}
