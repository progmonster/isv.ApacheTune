package com.apachetune.httpserver.ui.smartparts.about;

import com.apachetune.core.*;

import java.net.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
