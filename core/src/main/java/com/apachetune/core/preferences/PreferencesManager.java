package com.apachetune.core.preferences;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface PreferencesManager {
    Preferences systemNodeForPackage(Class<?> c);

    Preferences systemRoot();

    Preferences userNodeForPackage(Class<?> c);

    Preferences userRoot();
}
