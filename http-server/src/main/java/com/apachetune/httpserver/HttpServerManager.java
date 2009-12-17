package com.apachetune.httpserver;

import com.apachetune.httpserver.entities.*;

import java.io.*;
import java.net.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface HttpServerManager {
    HttpServer getServer(URI httpServerUri);

    boolean isHttpServerRootDirectory(File directory);

    File getServerDefaultInstallDirectory();
}
