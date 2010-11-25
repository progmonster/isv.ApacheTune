package com.apachetune.httpserver.entities.impl;

import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.entities.ServerObjectInfo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.apachetune.httpserver.entities.ServerObjectInfo.ServerObjectType.CONFIG_FILE;
import static java.io.File.separatorChar;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class LocalWindowsHttpServer implements HttpServer {
    private static final String MAIN_CONF_NAME = "httpd.conf";

    private static final String MAIN_CONF_PATH = "conf" + separatorChar + MAIN_CONF_NAME;

    private static final String EXTRA_CONF_PATH = "conf" + separatorChar + "extra";

    private static final String AUTOINDEX_CONF_NAME = "httpd-autoindex.conf";

    private static final String DAV_CONF_NAME = "httpd-dav.conf";

    private static final String DEFAULT_CONF_NAME = "httpd-default.conf";

    private static final String INFO_CONF_NAME = "httpd-info.conf";

    private static final String LANGUAGES_CONF_NAME = "httpd-languages.conf";

    private static final String MANUAL_CONF_NAME = "httpd-manual.conf";

    private static final String MPM_CONF_NAME = "httpd-mpm.conf";

    private static final String MULTILANG_ERRORDOC_CONF_NAME = "httpd-multilang-errordoc.conf";

    private static final String SSL_CONF_NAME = "httpd-ssl.conf";

    private static final String USERDIR_CONF_NAME = "httpd-userdir.conf";

    private static final String VHOSTS_CONF_NAME = "httpd-vhosts.conf";

    private static final String SERVER_APP_PATH = "bin" + separatorChar + "httpd.exe";

    private File serverRoot;

    public LocalWindowsHttpServer(File serverRoot) {
        notNull(serverRoot, "Argument serverRoot cannot be a null [this = " + this + "]");

        this.serverRoot = serverRoot;                        
    }

    public URI getUri() {
        return serverRoot.toURI();
    }

    public Process executeServerApp(String commandLineArguments) throws IOException {
        notNull(commandLineArguments, "Argument commandLineArguments cannot be a null [this = " + this + "]");

        return Runtime.getRuntime().exec(serverRoot.getAbsolutePath() + separatorChar + SERVER_APP_PATH + ' ' +
                commandLineArguments);
    }

    public File getServerRoot() {
        return serverRoot;           
    }

    public List<ServerObjectInfo> getServerObjectsInfo() {
        List<ServerObjectInfo> result = new ArrayList<ServerObjectInfo>();

        result.add(new ServerObjectInfo(this, new File(getServerRoot(), MAIN_CONF_PATH), CONFIG_FILE, MAIN_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), VHOSTS_CONF_NAME), CONFIG_FILE,
                VHOSTS_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), USERDIR_CONF_NAME), CONFIG_FILE,
                USERDIR_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), MULTILANG_ERRORDOC_CONF_NAME), CONFIG_FILE,
                MULTILANG_ERRORDOC_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), SSL_CONF_NAME), CONFIG_FILE, SSL_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), DEFAULT_CONF_NAME), CONFIG_FILE,
                DEFAULT_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), AUTOINDEX_CONF_NAME), CONFIG_FILE,
                AUTOINDEX_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), DAV_CONF_NAME), CONFIG_FILE, DAV_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), INFO_CONF_NAME), CONFIG_FILE,
                INFO_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), LANGUAGES_CONF_NAME), CONFIG_FILE,
                LANGUAGES_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), MANUAL_CONF_NAME), CONFIG_FILE,
                MANUAL_CONF_NAME));

        result.add(new ServerObjectInfo(this, new File(getExtraConfPath(), MPM_CONF_NAME), CONFIG_FILE, MPM_CONF_NAME));
        
        return result;
    }

    private File getExtraConfPath() {
        return new File(getServerRoot(), EXTRA_CONF_PATH);
    }
}
