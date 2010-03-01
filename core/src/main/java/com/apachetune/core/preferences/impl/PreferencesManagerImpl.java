package com.apachetune.core.preferences.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.PreferencesWrapper;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class PreferencesManagerImpl implements PreferencesManager {
    public Preferences systemNodeForPackage(Class<?> c) {
        return new PreferencesWrapper(java.util.prefs.Preferences.systemNodeForPackage(c));
    }

    public Preferences systemRoot() {
        return new PreferencesWrapper(java.util.prefs.Preferences.systemRoot());
    }

    public Preferences userNodeForPackage(Class<?> c) {
        return new PreferencesWrapper(java.util.prefs.Preferences.userNodeForPackage(c));
    }

    public Preferences userRoot() {
        return new PreferencesWrapper(java.util.prefs.Preferences.userRoot());
    }
}
