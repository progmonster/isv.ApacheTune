package com.apachetune.httpserver;

import java.net.URI;
import java.util.List;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface RecentOpenedServersManager {
    void storeServerUriToRecentList(URI serverUri);

    URI getLastOpenedServerUri();

    boolean hasLastOpenedServer();

    List<URI> getServerUriList();

    void addServerListChangedListener(RecentOpenedServerListChangedListener listener);

    void removeServerListChangedListener(RecentOpenedServerListChangedListener listener);

    void clearServerUriList();
}
