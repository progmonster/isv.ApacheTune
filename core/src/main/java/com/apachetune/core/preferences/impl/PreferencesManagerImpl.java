package com.apachetune.core.preferences.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.PreferencesWrapper;
import com.google.inject.Inject;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class PreferencesManagerImpl implements PreferencesManager {
    private final AppManager appManager;

    @Inject
    public PreferencesManagerImpl(final AppManager appManager) {
        this.appManager = appManager;
    }

    public Preferences systemNodeForPackage(Class<?> c) {
        return new PreferencesWrapper(java.util.prefs.Preferences.systemNodeForPackage(c).node(appManager
                .getFullAppName()));
    }

    public Preferences systemRoot() {
        return new PreferencesWrapper(java.util.prefs.Preferences.systemRoot());
    }

    public Preferences userNodeForPackage(Class<?> c) {
        return new PreferencesWrapper(java.util.prefs.Preferences.userNodeForPackage(c).node(appManager
                .getFullAppName()));
    }

    public Preferences userRoot() {
        return new PreferencesWrapper(java.util.prefs.Preferences.userRoot());
    }
}
