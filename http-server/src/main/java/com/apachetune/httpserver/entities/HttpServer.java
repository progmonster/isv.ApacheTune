package com.apachetune.httpserver.entities;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface HttpServer {
    URI getUri();

    File getServerRoot();

    List<ServerObjectInfo> getServerObjectsInfo();

    Process executeServerApp(String commandLineArguments) throws IOException;
}
