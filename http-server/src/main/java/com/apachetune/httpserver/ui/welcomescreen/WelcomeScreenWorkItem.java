package com.apachetune.httpserver.ui.welcomescreen;

import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.CoreUIWorkItem;
import com.apachetune.core.ui.GenericUIWorkItem;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.swing.*;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.httpserver.Constants.WELCOME_SCREEN_WORK_ITEM;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 *         Created Date: 18.03.2010
 */
public class WelcomeScreenWorkItem extends GenericUIWorkItem {
    private final CoreUIWorkItem coreUIWorkItem;

    @Inject
    public WelcomeScreenWorkItem(@Named(CORE_UI_WORK_ITEM) WorkItem coreUIWorkItem) {
        super(WELCOME_SCREEN_WORK_ITEM);

        this.coreUIWorkItem = (CoreUIWorkItem) coreUIWorkItem;
    }

    protected void doUIInitialize() {
            JPanel mainPanel = new WelcomeScreenView().getMainPanel();

            coreUIWorkItem.switchToWelcomeScreen(mainPanel);
    }

    protected void doUIDispose() {
        coreUIWorkItem.switchToToolWindowManager();
    }
}
