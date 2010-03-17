package com.apachetune.main;

import com.apachetune.core.AppManager;
import com.apachetune.core.LicenseManager;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.impl.AppManagerImpl;
import com.apachetune.core.impl.LicenseManagerImpl;
import com.apachetune.core.impl.RootWorkItemImpl;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.impl.PreferencesManagerImpl;
import com.google.inject.AbstractModule;

import static com.google.inject.Scopes.SINGLETON;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class MainModule extends AbstractModule {
    protected void configure() {
        bind(RootWorkItem.class).to(RootWorkItemImpl.class).in(SINGLETON);

        bind(AppManager.class).to(AppManagerImpl.class).in(SINGLETON);

        bind(LicenseManager.class).to(LicenseManagerImpl.class).in(SINGLETON);

        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(SINGLETON);
    }
}
