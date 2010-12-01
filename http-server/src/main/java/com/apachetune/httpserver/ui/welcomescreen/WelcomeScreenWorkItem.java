package com.apachetune.httpserver.ui.welcomescreen;

import com.apachetune.core.ui.GenericUIWorkItem;
import com.google.inject.Inject;
import com.google.inject.Provider;

import static com.apachetune.httpserver.Constants.WELCOME_SCREEN_WORK_ITEM;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 *         Created Date: 18.03.2010
 */
public class WelcomeScreenWorkItem extends GenericUIWorkItem {
    private final Provider<WelcomeScreenSmartPart> welcomeScreenSmartPartProvider;

    private WelcomeScreenSmartPart welcomeScreenSmartPart;

    @Inject
    public WelcomeScreenWorkItem(Provider<WelcomeScreenSmartPart> welcomeScreenSmartPartProvider) {
        super(WELCOME_SCREEN_WORK_ITEM);

        this.welcomeScreenSmartPartProvider = welcomeScreenSmartPartProvider;
    }

    protected void doUIInitialize() {
        welcomeScreenSmartPart = welcomeScreenSmartPartProvider.get();

        welcomeScreenSmartPart.initialize(this);
    }

    @Override
    protected void doUIDispose() {
        welcomeScreenSmartPart.dispose();
    }
}
