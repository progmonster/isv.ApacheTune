package com.apachetune.core;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface AppManager {
    UUID getAppInstallationUid();

    String getName();

    AppVersion getVersion();

    Date getBuildDate();

    Date getDevelopmentStartDate();

    String getVendor();

    URL getWebSite();

    String getCopyrightText();

    String getFullAppName();

    String getProductWebPortalUri();
}
