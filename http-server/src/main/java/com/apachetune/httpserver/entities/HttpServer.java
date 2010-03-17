package com.apachetune.httpserver.entities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface HttpServer {
    URI getUri();

    File getServerRoot();

    List<ServerObjectInfo> getServerObjectsInfo();

    Process executeServerApp(String commandLineArguments) throws IOException;
}
