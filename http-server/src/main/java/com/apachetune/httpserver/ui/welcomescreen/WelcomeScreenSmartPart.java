package com.apachetune.httpserver.ui.welcomescreen;

import chrriis.common.WebServer;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import com.apachetune.core.AppManager;
import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.CoreUIWorkItem;
import com.apachetune.core.ui.NSmartPart;
import com.apachetune.core.ui.UIWorkItem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 *         Created Date: 18.03.2010
 */
public class WelcomeScreenSmartPart implements VelocityContextProvider, NSmartPart, WelcomeScreenView {
    private static final String START_PAGE_RELATIVE_URL = "index.html.vm";

    private final WelcomeScreenPresenter presenter;

    private final AppManager appManager;

    private final CoreUIWorkItem coreUIWorkItem;

    private JPanel mainPanel;

    private JWebBrowser browser;

    private final Object listLock = new Object();

    @Inject
    public WelcomeScreenSmartPart(final WelcomeScreenPresenter presenter,
                                  final @Named(CORE_UI_WORK_ITEM) WorkItem coreUIWorkItem,
                                  final AppManager appManager) {
        this.presenter = presenter;
        this.appManager = appManager;
        this.coreUIWorkItem = (CoreUIWorkItem) coreUIWorkItem;

        WebServer.getDefaultWebServer().addContentProvider(new WebServer.WebServerContentProvider() {
            @Override
            public WebServer.WebServerContent getWebServerContent(WebServer.HTTPRequest httpRequest) {
                synchronized (listLock) {
                    return new DefaultWebServerContent(httpRequest, WelcomeScreenSmartPart.this);
                }
            }
        });
    }

    @Override
    public VelocityContext getVelocityContext() {
            VelocityContext ctx = new VelocityContext();

            return ctx;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void setRecentOpenedServerList(List<URI> serverUriList) {
       // TODO Recent list does not supported in this version. Implement it
    }

    @Override
    public void initialize(final UIWorkItem workItem) {
        notNull(workItem, "[this=" + this + ']');

        presenter.initialize(workItem, this);

        coreUIWorkItem.switchToWelcomeScreen(getMainPanel());

        browser = new JWebBrowser();

        browser.setDefaultPopupMenuRegistered(false);

        browser.addWebBrowserListener(new WebBrowserAdapter() {
            public void commandReceived(final WebBrowserCommandEvent e) {
                String cmd = e.getCommand();

                if (cmd.equals("openServer")) {
                    presenter.OnShowOpenServerDialog();
                } else if (cmd.equals("searchServer")) {
                    presenter.OnShowSearchServerDialog();
                } else if (cmd.equals("openProductWebPortal")) {
                    try {
                        Desktop.getDesktop().browse(new URI(appManager.getProductWebPortalUri()));
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } else {
                    // TODO Make it as a service.
                    throw new RuntimeException("Unknown command [command = '" + cmd + "']");
                }
            }
        }
        );

        mainPanel.add(browser);

        browser.setBarsVisible(false);

        presenter.onViewReady();
    }

    @Override
    public final void run() {
        // No-op.
    }

    @Override
    public void close() {
        presenter.onCloseView();

        presenter.dispose();
    }

    private void createUIComponents() {
        mainPanel = new JPanel();

        mainPanel.setLayout(new GridLayout());
    }

    public void reloadStartPage() {
        // No-op.
    }

    public void openStartPage() {
        browser.navigate(getStartPageUrl());
    }

    private String getStartPageUrl() {
        return WebServer.getDefaultWebServer().getURLPrefix() + "/" + START_PAGE_RELATIVE_URL;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}

class DefaultWebServerContent extends WebServer.WebServerContent {
    private final WebServer.HTTPRequest request;
    
    private final VelocityContextProvider velocityContextProvider;

    public DefaultWebServerContent(WebServer.HTTPRequest request, VelocityContextProvider velocityContextProvider) {
        notNull(request);
        notNull(velocityContextProvider);

        this.request = request;
        this.velocityContextProvider = velocityContextProvider;
    }

    @Override
    public String getContentType() {
        String ext = getResourceExtension();

        if (ext.equals("vm")) {
            return "text/html"; 
        } else {
            return getDefaultMimeType(ext);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            InputStream contentIS = getClass().getResourceAsStream("." + request.getResourcePath());

            if (getResourceExtension().equals("vm")) {
                Reader contentReader = new InputStreamReader(contentIS, "UTF-8");

                return new ByteArrayInputStream(fillVelocityTemplate(contentReader).getBytes("UTF-8"));
            } else {
                return contentIS;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    private String fillVelocityTemplate(Reader reader) {
        try {
            StringWriter writer = new StringWriter();

            Velocity.setProperty(RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
            Velocity.setProperty("runtime.log.logsystem.log4j.logger", "velocity_logger");

            Velocity.init();

            // todo "stdout" to constant.
            VelocityContext ctx = velocityContextProvider.getVelocityContext();

            boolean isOk = Velocity.evaluate(ctx, writer, "stdout", reader);

            isTrue(isOk);

            writer.close();

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.            
        }
    }

    private String getResourceExtension() {
        int dotIdx = request.getResourcePath().lastIndexOf(".");

        if (dotIdx == -1) {
            return "";
        } else {
            return request.getResourcePath().substring(dotIdx + 1);
        }
    }
}

interface VelocityContextProvider {
    VelocityContext getVelocityContext();
}