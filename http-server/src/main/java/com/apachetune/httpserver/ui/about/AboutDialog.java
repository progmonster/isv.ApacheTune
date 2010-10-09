package com.apachetune.httpserver.ui.about;

import com.apachetune.core.AppVersion;

import java.net.URL;
import java.util.Date;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface AboutDialog {
    void setProductName(String name);

    void setProductVersion(AppVersion version);

    void setProductBuildDate(Date buildDate);

    void setProductVendor(String vendor);

    void setProductWebSite(URL productWebSite);

    void setProductCopyrightText(String copyrightText);

    void setProductOwner(String owner);

}
