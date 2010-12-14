package com.apachetune.errorreportsystem;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.Constants.REMOTE_SERVICE_USER_EMAIL_PROP_NAME;
import static com.apachetune.core.utils.Utils.createRuntimeException;

/**
 * FIXDOC
 */
public class ErrorReportManager {
    private static final ErrorReportManager INSTANCE = new ErrorReportManager();

    public static ErrorReportManager getInstance() {
        return INSTANCE;
    }

    public final void sendErrorReport(Component parentComponent, String message, Throwable cause,
                                      PreferencesManager preferencesManager) {
        JOptionPane.showMessageDialog(parentComponent, cause.getMessage(), message, JOptionPane.ERROR_MESSAGE);
        //         // preferencesManager can be null

        // TODO implement
        // TODO send app log and delete it        
    }

    
    public final String getUserEmail(PreferencesManager preferencesManager) {
        //         // preferencesManager can be null
        Preferences prefs = preferencesManager.userNodeForPackage(ErrorReportManager.class);

        return prefs.get(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, null);
    }


    public final void storeUserEMail(String userEMail, PreferencesManager preferencesManager) {
        //         // preferencesManager can be null
        Preferences prefs = preferencesManager.userNodeForPackage(ErrorReportManager.class);

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


    private void sendAppLog(PreferencesManager preferencesManager) {
        //         // preferencesManager can be null

        // TODO implement
    }
}
