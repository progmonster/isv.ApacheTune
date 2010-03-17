package com.apachetune.httpserver;

import com.apachetune.httpserver.entities.HttpServer;

import java.io.File;
import java.net.URI;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface HttpServerManager {
    HttpServer getServer(URI httpServerUri);

    boolean isHttpServerRootDirectory(File directory);

    File getServerDefaultInstallDirectory();
}
