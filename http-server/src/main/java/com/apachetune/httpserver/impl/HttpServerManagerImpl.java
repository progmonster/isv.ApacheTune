package com.apachetune.httpserver.impl;

import com.apachetune.httpserver.HttpServerManager;
import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.entities.impl.LocalWindowsHttpServer;

import java.io.File;
import java.net.URI;

import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpServerManagerImpl implements HttpServerManager {
    // TODO Remove Windows-specific code.
    private static final String SERVER_DEFAULT_INSTALL_PATH = "C:\\Program Files\\Apache Software Foundation" +
            "\\Apache2.2";

    public HttpServer getServer(URI httpServerUri) {
        // TODO Now this implementation returns a local windows server objects only.

        notNull(httpServerUri, "Argument httpServerUri cannot be a null [this = " + this + "]");

        return new LocalWindowsHttpServer(new File(httpServerUri));
    }

    public boolean isHttpServerRootDirectory(File directory) {
        // TODO Now this implementation returns a local windows server objects only.

        notNull(directory, "Argument directory cannot be a null [this = " + this + "]");

        return new File(directory, "bin" + File.separatorChar + "httpd.exe").exists() && new File(directory, "conf" +
                File.separatorChar + "httpd.conf").exists();
    }

    public File getServerDefaultInstallDirectory() {
        // TODO Now this implementation returns a local windows server objects only.

        return new File(SERVER_DEFAULT_INSTALL_PATH);
    }
}
