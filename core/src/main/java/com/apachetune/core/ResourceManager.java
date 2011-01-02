package com.apachetune.core;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * FIXDOC
 */
public class ResourceManager {
    private static final ResourceManager INSTANCE = new ResourceManager();

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public final ResourceBundle getResourceBundle(Class clazz) {
        String bundleBaseName = clazz.getPackage().getName() + '.' + "messages"; //NON-NLS

        return ResourceBundle.getBundle(bundleBaseName, Locale.US);
    }
}
