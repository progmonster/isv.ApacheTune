package com.apachetune.core.preferences;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface PreferencesManager {
    Preferences systemNodeForPackage(Class<?> c);

    Preferences systemRoot();

    Preferences userNodeForPackage(Class<?> c);

    Preferences userRoot();
}
