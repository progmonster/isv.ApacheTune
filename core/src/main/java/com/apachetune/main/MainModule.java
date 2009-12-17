package com.apachetune.main;

import com.apachetune.core.*;
import com.apachetune.core.preferences.*;
import com.apachetune.core.preferences.impl.*;
import com.apachetune.core.impl.*;
import com.google.inject.*;
import static com.google.inject.Scopes.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
