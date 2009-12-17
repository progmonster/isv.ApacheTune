package com.apachetune.core.impl;

import com.apachetune.core.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class AppManagerImpl implements AppManager {
    private static final String APP_PROPERTIES_PATH = "/app.properties";

    private static final String APP_PROPERTIES_ENCODING = "utf-8";

    private static final String NAME_PROP = "name";

    private static final String VERSION_PROP = "version";

    private static final String DEVELOPMENT_START_DATE_PROP = "developmentStartDate";

    private static final String COPYRIGHT_TEXT_PROP = "copyright";

    private static final String WEBSITE_PROP = "website";

    private static final String VENDOR_PROP = "vendor";

    private static final String BUILD_DATE_PROP = "buildDate";
    private Properties appProps;

    public AppManagerImpl() {
        try {
            appProps = new Properties();

            appProps.load(new InputStreamReader(getClass().getResourceAsStream(APP_PROPERTIES_PATH),
                    APP_PROPERTIES_ENCODING));
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public String getName() {
        return appProps.getProperty(NAME_PROP);
    }

    public AppVersion getVersion() {
        return new AppVersion(appProps.getProperty(VERSION_PROP));
    }

    public Date getBuildDate() {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(appProps.getProperty(BUILD_DATE_PROP));
        } catch (ParseException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public Date getDevelopmentStartDate() {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(appProps.getProperty(DEVELOPMENT_START_DATE_PROP));
        } catch (ParseException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public String getVendor() {
        return appProps.getProperty(VENDOR_PROP);
    }

    public URL getWebSite() {
        try {
            return new URL(appProps.getProperty(WEBSITE_PROP));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public String getCopyrightText() {
        // This method does not allow for a time zones, but it's not so important here. 

        Calendar startDevDate = Calendar.getInstance();

        startDevDate.setTime(getDevelopmentStartDate());

        int startDevYear = startDevDate.get(Calendar.YEAR);

        Calendar buildDate = Calendar.getInstance();

        buildDate.setTime(getBuildDate());

        int buildDateYear = buildDate.get(Calendar.YEAR);

        String copyrightYears;

        if (buildDateYear == startDevYear) {
            copyrightYears = String.valueOf(buildDateYear);
        } else {
            copyrightYears = String.valueOf(startDevYear) + '-' + String.valueOf(buildDateYear);
        }

        String copyrightTemplate = appProps.getProperty(COPYRIGHT_TEXT_PROP);

        return MessageFormat.format(copyrightTemplate, copyrightYears); 
    }
}
