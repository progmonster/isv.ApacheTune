package com.apachetune.feedbacksystem.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.feedbacksystem.FeedbackManager;
import com.google.inject.Inject;

import java.util.prefs.BackingStoreException;

import static com.apachetune.core.Constants.REMOTE_SERVICE_USER_EMAIL_PROP_NAME;
import static com.apachetune.core.utils.Utils.createRuntimeException;

/**
 * FIXDOC
 */
public class FeedbackManagerImpl implements FeedbackManager {
    private final PreferencesManager preferencesManager;

    @Inject
    public FeedbackManagerImpl(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public final void sendErrorReport(String message, Throwable cause) {
        // TODO implement
    }

    @Override
    public final void sendAppLog() {
        // TODO implement
    }

    @Override
    public final String getUserEmail() {
        Preferences prefs = preferencesManager.userNodeForPackage(FeedbackManagerImpl.class);

        return prefs.get(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, null);
    }

    @Override
    public final void storeUserEMail(String userEMail) {
        Preferences prefs = preferencesManager.userNodeForPackage(FeedbackManagerImpl.class);

        if (userEMail != null) {
            prefs.put(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, userEMail);
        } else {
            prefs.remove(REMOTE_SERVICE_USER_EMAIL_PROP_NAME);
        }

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }
}
