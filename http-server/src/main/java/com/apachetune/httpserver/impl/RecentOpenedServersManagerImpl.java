package com.apachetune.httpserver.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.httpserver.RecentOpenedServerListChangedListener;
import com.apachetune.httpserver.RecentOpenedServersManager;
import com.google.inject.Inject;
import org.apache.commons.lang.ArrayUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.httpserver.Constants.RECENT_OPENED_SERVER;
import static java.lang.StrictMath.min;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

public class RecentOpenedServersManagerImpl implements RecentOpenedServersManager {
    static final int RECENT_LIST_SIZE = 5;

    private final PreferencesManager preferencesManager;

    private final List<RecentOpenedServerListChangedListener> changeListener =
            new ArrayList<RecentOpenedServerListChangedListener>();


    @Inject
    public RecentOpenedServersManagerImpl(final PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;

        //noinspection ConstantConditions
        isTrue(RECENT_LIST_SIZE >= 0, "RECENT_LIST_SIZE can not be less than zero."); //NON-NLS

        truncateListToMaxSize();
    }

    public void storeServerUriToRecentList(URI serverUri) {
        notNull(serverUri, "Argument serverUri cannot be a null"); //NON-NLS

        List<URI> serverUriList = getServerUriList();

        serverUriList.remove(serverUri);

        serverUriList.add(0, serverUri);

        doUpdateServerUriList(serverUriList);

        doTruncateServerUriList();

        notifyChangeListeners();
    }

    public URI getLastOpenedServerUri() {
        isTrue(hasLastOpenedServer(), "It should be at least one previously opened http-server."); //NON-NLS

        Preferences node = preferencesManager.userNodeForPackage(getClass());

        String strServerUri = node.get(getServerItemKey(0), null);

        try {
            return new URI(strServerUri);
        } catch (URISyntaxException e) {
            throw createRuntimeException(e);
        }
    }

    public boolean hasLastOpenedServer() {
        Preferences node = preferencesManager.userNodeForPackage(getClass());

        try {
            return ArrayUtils.contains(node.keys(), getServerItemKey(0));
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }

    public void clearServerUriList() {
        doClearServerUriList();

        notifyChangeListeners();
    }

    public List<URI> getServerUriList() {
        List<URI> serverUriList = new ArrayList<URI>(RECENT_LIST_SIZE);

        Preferences node = preferencesManager.userNodeForPackage(getClass());

        int serverUriIdx = 0;

        String strServerUri;

        try {
            while ((strServerUri = node.get(getServerItemKey(serverUriIdx++), null)) != null) {
                serverUriList.add(new URI(strServerUri));
            }
        } catch (URISyntaxException e) {
            throw createRuntimeException(e);
        }

        return serverUriList;
    }

    public void addServerListChangedListener(RecentOpenedServerListChangedListener listener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(listener, "Argument listener cannot be a null"); //NON-NLS

        changeListener.add(listener);
    }

    @Override
    public void removeServerListChangedListener(RecentOpenedServerListChangedListener listener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(listener, "        Argument listener cannot be a null"); //NON-NLS

        changeListener.remove(listener);
    }

    private void doClearServerUriList() {
        Preferences node = preferencesManager.userNodeForPackage(getClass());

        try {
            node.removeNode();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }

    private void doUpdateServerUriList(List<URI> serverUriList) {
        doClearServerUriList();

        Preferences node = preferencesManager.userNodeForPackage(getClass());

        for (int serverUriIdx = 0; serverUriIdx < serverUriList.size(); serverUriIdx++) {
            node.put(getServerItemKey(serverUriIdx), serverUriList.get(serverUriIdx).toString());
        }

        try {
            node.flush();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }
    }

    private String getServerItemKey(int serverIdx) {
        return MessageFormat.format(RECENT_OPENED_SERVER, serverIdx);
    }

    private void notifyChangeListeners() {
        List<RecentOpenedServerListChangedListener> listeners = new ArrayList<RecentOpenedServerListChangedListener>(
                changeListener);

        for (RecentOpenedServerListChangedListener listener : listeners) {
            listener.onRecentOpenedServerListChanged();
        }
    }

    private void truncateListToMaxSize() {
        doTruncateServerUriList();

        notifyChangeListeners();
    }

    private void doTruncateServerUriList() {
        List<URI> serverUriList = getServerUriList();

        serverUriList = serverUriList.subList(0, min(serverUriList.size(), RECENT_LIST_SIZE));

        doUpdateServerUriList(serverUriList);
    }
}
