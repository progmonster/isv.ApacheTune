package com.apachetune.core;

import java.net.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
}
