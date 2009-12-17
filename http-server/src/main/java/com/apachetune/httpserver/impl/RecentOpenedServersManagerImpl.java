package com.apachetune.httpserver.impl;

import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.*;
import org.apache.commons.lang.*;

import static java.lang.StrictMath.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.prefs.*;

public class RecentOpenedServersManagerImpl implements RecentOpenedServersManager {
    private static final int RECENT_LIST_SIZE = 5;

    private final List<RecentOpenedServerListChangedListener> changeListener =
            new ArrayList<RecentOpenedServerListChangedListener>();


    public RecentOpenedServersManagerImpl() {
        //noinspection ConstantConditions
        if (RECENT_LIST_SIZE < 0) {
            throw new RuntimeException("RECENT_LIST_SIZE can not be less than zero."); 
        }

        truncateListToMaxSize();
    }

    public void storeServerUriToRecentList(URI serverUri) {
        if (serverUri == null) {
            throw new NullPointerException("Argument serverUri cannot be a null [this = " + this + "]");
        }

        List<URI> serverUriList = getServerUriList();

        serverUriList.remove(serverUri);

        serverUriList = serverUriList.subList(0, min(serverUriList.size(), RECENT_LIST_SIZE - 1));

        serverUriList.add(0, serverUri);

        updateServerUriList(serverUriList);

        notifyChangeListeners();
    }

    public URI getLastOpenedServerUri() {
        if (!hasLastOpenedServer()) {
            throw new IllegalStateException("It should be at least one previously opened http-server.");
        }

        Preferences node = Preferences.userNodeForPackage(getClass());

        String strServerUri = node.get(getServerItemKey(0), null);

        try {
            return new URI(strServerUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public boolean hasLastOpenedServer() {
        Preferences node = Preferences.userNodeForPackage(getClass());

        try {
            return ArrayUtils.contains(node.keys(), getServerItemKey(0));
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public void clearServerUriList() {
        doClearServerUriList();

        notifyChangeListeners();
    }

    public List<URI> getServerUriList() {
        List<URI> serverUriList = new ArrayList<URI>(RECENT_LIST_SIZE);

        Preferences node = Preferences.userNodeForPackage(getClass());

        int serverUriIdx = 0;

        String strServerUri;

        try {
            while ((strServerUri = node.get(getServerItemKey(serverUriIdx++), null)) != null) {
                serverUriList.add(new URI(strServerUri));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        return serverUriList;
    }

    public void addServerListChangedListener(RecentOpenedServerListChangedListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        changeListener.add(listener);
    }

    private void doClearServerUriList() {
        Preferences node = Preferences.userNodeForPackage(getClass());

        try {
            node.removeNode();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    private void updateServerUriList(List<URI> serverUriList) {
        doClearServerUriList();

        Preferences node = Preferences.userNodeForPackage(getClass());

        for (int serverUriIdx = 0; serverUriIdx < serverUriList.size(); serverUriIdx++) {
            node.put(getServerItemKey(serverUriIdx), serverUriList.get(serverUriIdx).toString());
        }

        try {
            node.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    private String getServerItemKey(int serverIdx) {
        return MessageFormat.format(RECENT_OPENED_SERVER, serverIdx);
    }

    private void notifyChangeListeners() {
        List<RecentOpenedServerListChangedListener> listeners = new ArrayList<RecentOpenedServerListChangedListener>(
                changeListener);

        for (RecentOpenedServerListChangedListener listener : listeners) {
            listener.onListChanged();
        }
    }

    private void truncateListToMaxSize() {
        List<URI> serverUriList = getServerUriList();

        serverUriList = serverUriList.subList(0, min(serverUriList.size(), RECENT_LIST_SIZE));

        updateServerUriList(serverUriList);

        notifyChangeListeners();
    }
}
