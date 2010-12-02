package com.apachetune.core.ui;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.utils.Utils.createRuntimeException;

/**
 * FIXDOC
 */
public class Utils {
    public static final String USER_FEEDBACK_SMART_PART_ID = "USER_FEEDBACK_SMART_PART";

    public static void restoreDialogBounds(PreferencesManager preferencesManager, String dialogId,
                                              JDialog dialog, int initialWidth, int initialHeight) {
        dialog.setMinimumSize(new Dimension(initialWidth, initialHeight));

        Preferences pref = preferencesManager.userNodeForPackage(Utils.class).node(dialogId);

        int left = pref.getInt("left", Integer.MAX_VALUE);

        int top = pref.getInt("top", Integer.MAX_VALUE);

        int width = pref.getInt("width", initialWidth);

        int height = pref.getInt("height", initialHeight);

        dialog.setSize(width, height);

        if ((left == Integer.MAX_VALUE) || (top == Integer.MAX_VALUE)) {
            dialog.setLocationRelativeTo(null);
        } else {
            dialog.setLocation(left, top);
        }
    }

    public static void storeDialogBounds(PreferencesManager preferencesManager, String dialogId, JDialog dialog) {
        Preferences pref = preferencesManager.userNodeForPackage(Utils.class).node(dialogId);

        pref.putInt("left", dialog.getLocation().x);
        pref.putInt("top", dialog.getLocation().y);
        pref.putInt("width", dialog.getSize().width);
        pref.putInt("height", dialog.getSize().height);

        try {
            pref.flush();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }   
}
