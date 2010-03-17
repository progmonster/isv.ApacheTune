package com.apachetune.core.preferences;

import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class PreferencesWrapper implements Preferences {
    private final java.util.prefs.Preferences preferences;

    public PreferencesWrapper(java.util.prefs.Preferences preferences) {
        if (preferences == null) {
            throw new NullPointerException("Argument preferences cannot be a null [this = " + this + "]");
        }

        this.preferences = preferences;
    }

    public void put(String key, String value) {
        preferences.put(key, value);
    }

    public String get(String key, String def) {
        return preferences.get(key, def);
    }

    public void remove(String key) {
        preferences.remove(key);
    }

    public void clear() throws BackingStoreException {
        preferences.clear();
    }

    public void putInt(String key, int value) {
        preferences.putInt(key, value);
    }

    public int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }

    public void putLong(String key, long value) {
        preferences.putLong(key, value);
    }

    public long getLong(String key, long def) {
        return preferences.getLong(key, def);
    }

    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    public void putFloat(String key, float value) {
        preferences.putFloat(key, value);
    }

    public float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }

    public void putDouble(String key, double value) {
        preferences.putDouble(key, value);
    }

    public double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }

    public void putByteArray(String key, byte[] value) {
        preferences.putByteArray(key, value);
    }

    public byte[] getByteArray(String key, byte[] def) {
        return preferences.getByteArray(key, def);
    }

    public String[] keys() throws BackingStoreException {
        return preferences.keys();
    }

    public String[] childrenNames() throws BackingStoreException {
        return preferences.childrenNames();
    }

    public Preferences parent() {
        return new PreferencesWrapper(preferences.parent());
    }

    public Preferences node(String pathName) {
        return new PreferencesWrapper(preferences.node(pathName));
    }

    public boolean nodeExists(String pathName) throws BackingStoreException {
        return preferences.nodeExists(pathName);
    }

    public void removeNode() throws BackingStoreException {
        preferences.removeNode();
    }

    public String name() {
        return preferences.name();
    }

    public String absolutePath() {
        return preferences.absolutePath();
    }

    public boolean isUserNode() {
        return preferences.isUserNode();
    }

    public void flush() throws BackingStoreException {
        preferences.flush();
    }

    public void sync() throws BackingStoreException {
        preferences.sync();
    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.addPreferenceChangeListener(pcl);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.removePreferenceChangeListener(pcl);
    }

    public void addNodeChangeListener(NodeChangeListener ncl) {
        preferences.addNodeChangeListener(ncl);
    }

    public void removeNodeChangeListener(NodeChangeListener ncl) {
        preferences.removeNodeChangeListener(ncl);
    }

    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportNode(os);
    }

    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportSubtree(os);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PreferencesWrapper) {
            PreferencesWrapper preferencesImpl = (PreferencesWrapper) obj;

            return preferences.equals(preferencesImpl.preferences);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return preferences.hashCode();
    }
}
