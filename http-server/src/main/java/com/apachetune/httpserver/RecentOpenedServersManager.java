package com.apachetune.httpserver;

import java.net.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface RecentOpenedServersManager {
    void storeServerUriToRecentList(URI serverUri);

    URI getLastOpenedServerUri();

    boolean hasLastOpenedServer();

    List<URI> getServerUriList();

    void addServerListChangedListener(RecentOpenedServerListChangedListener listener);

    void clearServerUriList();
}
