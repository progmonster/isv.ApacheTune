package com.apachetune.core.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.AppVersion;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.utils.Utils;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.Constants.APP_INSTALLATION_UID_PROP;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static java.util.Locale.ENGLISH;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class AppManagerImpl implements AppManager {
    private static final String APP_PROPERTIES_PATH = "/app.properties"; //NON-NLS

    private static final String APP_PROPERTIES_ENCODING = "utf-8"; //NON-NLS

    private static final String NAME_PROP = "name"; //NON-NLS

    private static final String VERSION_PROP = "version"; //NON-NLS

    private static final String DEVELOPMENT_START_DATE_PROP = "developmentStartDate"; //NON-NLS

    private static final String COPYRIGHT_TEXT_PROP = "copyright"; //NON-NLS

    private static final String WEBSITE_PROP = "website"; //NON-NLS

    private static final String VENDOR_PROP = "vendor"; //NON-NLS

    private static final String BUILD_DATE_PROP = "buildDate"; //NON-NLS

    private static final String PRODUCT_WEB_PORTAL_URI_PROP = "productWebPortalUri"; //NON-NLS

    public static final String APP_BUILD_DATE_FORMAT = "dd-MM-yyyy"; //NON-NLS

    private final PreferencesManager preferencesManager;

    private Properties appProps;

    @Inject
    public AppManagerImpl(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
        
        try {
            appProps = new Properties();

            appProps.load(new InputStreamReader(getClass().getResourceAsStream(APP_PROPERTIES_PATH),
                    APP_PROPERTIES_ENCODING));
        } catch (IOException e) {
            throw createRuntimeException(e);
        }
    }

    public UUID getAppInstallationUid() {
        Preferences prefs = preferencesManager.userNodeForPackage(AppManagerImpl.class);

        String strUid = prefs.get(APP_INSTALLATION_UID_PROP, null);

        if (strUid == null) {
            strUid = UUID.randomUUID().toString();

            prefs.put(APP_INSTALLATION_UID_PROP, strUid);

            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                throw createRuntimeException(e);
            }
        }

        return UUID.fromString(strUid);
    }

    public String getName() {
        return appProps.getProperty(NAME_PROP);
    }

    public AppVersion getVersion() {
        return new AppVersion(appProps.getProperty(VERSION_PROP));
    }

    public Date getBuildDate() {
        try {
            return new SimpleDateFormat(APP_BUILD_DATE_FORMAT, ENGLISH).parse(appProps.getProperty(BUILD_DATE_PROP));
        } catch (ParseException e) {
            throw Utils.createRuntimeException(e);
        }
    }

    public Date getDevelopmentStartDate() {
        try {
            return new SimpleDateFormat(APP_BUILD_DATE_FORMAT, ENGLISH)
                    .parse(appProps.getProperty(DEVELOPMENT_START_DATE_PROP));
        } catch (ParseException e) {
            throw Utils.createRuntimeException(e);
        }
    }

    public String getVendor() {
        return appProps.getProperty(VENDOR_PROP);
    }

    public URL getWebSite() {
        try {
            return new URL(appProps.getProperty(WEBSITE_PROP));
        } catch (MalformedURLException e) {
            throw Utils.createRuntimeException(e);
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
            //noinspection MagicCharacter,StringConcatenation
            copyrightYears = String.valueOf(startDevYear) + '-' + String.valueOf(buildDateYear);
        }

        String copyrightTemplate = appProps.getProperty(COPYRIGHT_TEXT_PROP);

        return MessageFormat.format(copyrightTemplate, copyrightYears); 
    }

    public String getFullAppName() {
        //noinspection MagicCharacter,StringConcatenation
        return getName() + '-' + getVersion().format();
    }

    public String getProductWebPortalUri() {
        return appProps.getProperty(PRODUCT_WEB_PORTAL_URI_PROP);
    }
}
