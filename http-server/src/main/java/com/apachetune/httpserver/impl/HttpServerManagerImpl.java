package com.apachetune.httpserver.impl;

import com.apachetune.httpserver.*;
import com.apachetune.httpserver.entities.*;
import com.apachetune.httpserver.entities.impl.*;

import java.io.*;
import java.net.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpServerManagerImpl implements HttpServerManager {
    // TODO Remove Windows-specific code.
    private static final String SERVER_DEFAULT_INSTALL_PATH = "C:\\Program Files\\Apache Software Foundation" +
            "\\Apache2.2";

    public HttpServer getServer(URI httpServerUri) {
        // TODO Now this implementation returns a local windows server objects only.

        if (httpServerUri == null) {
            throw new NullPointerException("Argument httpServerUri cannot be a null [this = " + this + "]");
        }
        
        return new LocalWindowsHttpServer(new File(httpServerUri));
    }

    public boolean isHttpServerRootDirectory(File directory) {
        // TODO Now this implementation returns a local windows server objects only.

        if (directory == null) {
            throw new NullPointerException("Argument directory cannot be a null [this = " + this + "]");
        }

        return new File(directory, "bin" + File.separatorChar + "httpd.exe").exists() && new File(directory, "conf" +
                File.separatorChar + "httpd.conf").exists();
    }

    public File getServerDefaultInstallDirectory() {
        // TODO Now this implementation returns a local windows server objects only.

        return new File(SERVER_DEFAULT_INSTALL_PATH);
    }
}
