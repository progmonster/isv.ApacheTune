package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.httpserver.ui.updating.UpdateConfiguration;
import com.google.inject.Inject;

import java.util.prefs.BackingStoreException;

import static com.apachetune.httpserver.Constants.IS_CHECK_FOR_UPDATE_ON_START_ENABLED_PREF_NAME;

/**
 * FIXDOC
 */
public class UpdateConfigurationImpl implements UpdateConfiguration {
    private final PreferencesManager preferencesManager;

    @Inject
    public UpdateConfigurationImpl(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public final void storeCheckUpdateFlag(boolean value) {
        Preferences prefs = getPrefs();

        prefs.putBoolean(IS_CHECK_FOR_UPDATE_ON_START_ENABLED_PREF_NAME, value);

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error.", e);
        }
    }

    @Override
    public final boolean getCheckUpdateFlag() {
        Preferences prefs = getPrefs();

        return prefs.getBoolean(IS_CHECK_FOR_UPDATE_ON_START_ENABLED_PREF_NAME, true);
    }

    private Preferences getPrefs() {
        return preferencesManager.userNodeForPackage(UpdateConfigurationImpl.class);
    }
}
