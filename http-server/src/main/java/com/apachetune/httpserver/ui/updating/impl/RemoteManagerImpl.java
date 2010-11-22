package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.ApplicationException;
import com.apachetune.httpserver.ui.updating.RemoteManager;
import com.apachetune.httpserver.ui.updating.UpdateInfo;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import static com.apachetune.core.utils.Utils.getChildElementContent;
import static com.apachetune.httpserver.Constants.REMOTE_UPDATE_SERVICE_URL_PROP;
import static java.text.MessageFormat.format;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.apache.commons.httpclient.params.HttpMethodParams.RETRY_HANDLER;

/**
 * FIXDOC
 */
public class RemoteManagerImpl implements RemoteManager {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImpl.class);

    private final String remoteUpdateServiceUrl;

    private final AppManager appManager;

    @Inject
    public RemoteManagerImpl(@Named(REMOTE_UPDATE_SERVICE_URL_PROP) String remoteUpdateServiceUrl,
                             AppManager appManager) {
        this.remoteUpdateServiceUrl = remoteUpdateServiceUrl;
        this.appManager = appManager;
    }

    @Override
    public final UpdateInfo checkUpdateAvailable() {
        HttpClient client = new HttpClient();

        HttpMethod method = new GetMethod(remoteUpdateServiceUrl);

        method.getParams().setParameter(RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        method.setQueryString(format("action=check-for-updates&app-fullname={1}", appManager.getFullAppName()));

        UpdateInfo updateInfo = UpdateInfo.createNoUpdateInfo();

        int resultCode;

        try {
            resultCode = client.executeMethod(method);

            if (resultCode == SC_OK) {
                String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");

                updateInfo = parseResponse(response);
            } else {
                logger.error("Remote update service returned error response code [code=" + resultCode + ']');
            }
        } catch (IOException e) {
            logger.error("Error getting update info from remote", e);
        }

        method.releaseConnection();

        return updateInfo;
    }

    private UpdateInfo parseResponse(String response) {
        UpdateInfo updateInfo = UpdateInfo.createNoUpdateInfo();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new StringReader(response));

            Document doc = db.parse(is);

            Element docElem = doc.getDocumentElement();

            NodeList updateElems = docElem.getElementsByTagName("update");

            Element updateElem;

            if (updateElems.getLength() > 0) {
                updateElem = (Element) updateElems.item(0);

                updateInfo = parseUpdateInfoItem(updateElem);
            }
        } catch (Throwable cause) {
            logger.error("Error during parsing update info from remote", cause);
        }

        return updateInfo;
    }

    private UpdateInfo parseUpdateInfoItem(Element updateElem)
            throws ApplicationException, MalformedURLException, UnsupportedEncodingException {
        String userFriendlyFullAppName = getChildElementContent(updateElem, "userFriendlyFullAppName");

        String userFriendlyUpdateWebPageEncodedUrl =
                getChildElementContent(updateElem, "userFriendlyUpdateWebPageEncodedUrl");

        String userFriendlyUpdateWebPageUrl = URLDecoder.decode(userFriendlyUpdateWebPageEncodedUrl, "UTF-8");

        return UpdateInfo.create(userFriendlyFullAppName, new URL(userFriendlyUpdateWebPageUrl));
    }
}
