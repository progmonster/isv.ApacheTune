package com.apachetune.core;

import java.net.URL;
import java.util.Date;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface AppManager {
    String getName();

    AppVersion getVersion();

    Date getBuildDate();

    Date getDevelopmentStartDate();

    String getVendor();

    URL getWebSite();

    String getCopyrightText();

    String getFullAppName();
}
