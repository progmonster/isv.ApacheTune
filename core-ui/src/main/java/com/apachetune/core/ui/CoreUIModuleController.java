package com.apachetune.core.ui;

import com.apachetune.core.AppManager;
import com.apachetune.core.ModuleController;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.preferences.PreferencesManager;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.core.utils.Utils.showSendErrorReportDialog;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CoreUIModuleController implements ModuleController {
    private static final Logger logger = LoggerFactory.getLogger(CoreUIModuleController.class);

    private final Module coreUIModule = new CoreUIModule();

    @Inject @Named(CORE_UI_WORK_ITEM)
    private UIWorkItem coreUIWorkItem;

    @Inject
    private AppManager appManager;

    @Inject 
    private PreferencesManager preferencesManager;

    public void initialize(RootWorkItem rootWorkItem) {
        notNull(rootWorkItem, "Argument rootWorkItem cannot be a null");

        rootWorkItem.addChildWorkItem(coreUIWorkItem);

        setEDTLogger();
    }

    public Module getModule() {
        return coreUIModule;
    }

    private void setEDTLogger() {
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

        eventQueue.push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                try {
                    super.dispatchEvent(event);
                } catch (Throwable cause) {
                    logger.error("Error in app", cause);

                    JFrame mainFrame = null;

                    if (coreUIWorkItem != null) {
                        mainFrame = ((CoreUIWorkItem) coreUIWorkItem).getMainFrame();
                    }
                    
                    showSendErrorReportDialog(mainFrame, "Application error", cause, appManager, preferencesManager);
                }
            }
        });
    }
}
