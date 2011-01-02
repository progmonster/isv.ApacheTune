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
    public static final String USER_FEEDBACK_SMART_PART_ID = "USER_FEEDBACK_SMART_PART"; //NON-NLS

    public static void restoreDialogBounds(PreferencesManager preferencesManager, String dialogId,
                                              JDialog dialog, int initialWidth, int initialHeight) {
        dialog.setMinimumSize(new Dimension(initialWidth, initialHeight));

        Preferences pref = preferencesManager.userNodeForPackage(Utils.class).node(dialogId);

        //noinspection DuplicateStringLiteralInspection
        int left = pref.getInt("left", Integer.MAX_VALUE); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        int top = pref.getInt("top", Integer.MAX_VALUE); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        int width = pref.getInt("width", initialWidth); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        int height = pref.getInt("height", initialHeight); //NON-NLS

        dialog.setSize(width, height);

        if ((left == Integer.MAX_VALUE) || (top == Integer.MAX_VALUE)) {
            dialog.setLocationRelativeTo(null);
        } else {
            dialog.setLocation(left, top);
        }
    }

    public static void storeDialogBounds(PreferencesManager preferencesManager, String dialogId, JDialog dialog) {
        Preferences pref = preferencesManager.userNodeForPackage(Utils.class).node(dialogId);

        //noinspection DuplicateStringLiteralInspection
        pref.putInt("left", dialog.getLocation().x); //NON-NLS
        //noinspection DuplicateStringLiteralInspection
        pref.putInt("top", dialog.getLocation().y); //NON-NLS
        //noinspection DuplicateStringLiteralInspection
        pref.putInt("width", dialog.getSize().width); //NON-NLS
        //noinspection DuplicateStringLiteralInspection
        pref.putInt("height", dialog.getSize().height); //NON-NLS

        try {
            pref.flush();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }   
}
