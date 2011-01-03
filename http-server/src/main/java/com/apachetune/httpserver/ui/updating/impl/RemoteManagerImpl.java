package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.ApplicationException;
import com.apachetune.httpserver.ui.updating.RemoteManager;
import com.apachetune.httpserver.ui.updating.UpdateException;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    public final UpdateInfo checkUpdateAvailable() throws UpdateException {
        HttpClient client = new HttpClient();

        HttpMethod method = new GetMethod(remoteUpdateServiceUrl);

        method.getParams().setParameter(RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        method.setQueryString(format("action=check-for-updates&app-fullname={0}", //NON-NLS
                appManager.getFullAppName()));

        try {
            int resultCode = client.executeMethod(method);

            if (resultCode == SC_OK) {
                String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8"); //NON-NLS

                return parseResponse(response);
            } else {
                throw new UpdateException(
                        "Remote update service returned error response code [code=" + resultCode + ']'); //NON-NLS
            }
        } catch (IOException e) {
            throw new UpdateException("Error getting update info from remote", e); //NON-NLS
        } finally {
            method.releaseConnection();
        }
    }

    private UpdateInfo parseResponse(String response) throws UpdateException {
        UpdateInfo updateInfo = UpdateInfo.createNoUpdateInfo();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Internal error.", e); //NON-NLS

            return updateInfo;
        }

        InputSource is = new InputSource(new StringReader(response));

        Document doc;
        
        try {
            doc = db.parse(is);
        } catch (SAXException e) {
            //noinspection DuplicateStringLiteralInspection
            throw new UpdateException("Error during parsing update info.", e); //NON-NLS
        } catch (IOException e) {
            //noinspection DuplicateStringLiteralInspection
            throw new UpdateException("Error during parsing update info.", e); //NON-NLS
        }

        Element docElem = doc.getDocumentElement();

        NodeList updateElems = docElem.getElementsByTagName("update"); //NON-NLS

        Element updateElem;

        if (updateElems.getLength() > 0) {
            updateElem = (Element) updateElems.item(0);

            updateInfo = parseUpdateInfoItem(updateElem);
        }

        return updateInfo;
    }

    private UpdateInfo parseUpdateInfoItem(Element updateElem) throws UpdateException {
        try {
            @SuppressWarnings({"DuplicateStringLiteralInspection"}) String userFriendlyFullAppName =
                    getChildElementContent(updateElem, "userFriendlyFullAppName").trim(); //NON-NLS

            String userFriendlyUpdateWebPageEncodedUrl =
                    getChildElementContent(updateElem, "userFriendlyUpdateWebPageEncodedUrl").trim(); //NON-NLS

            String userFriendlyUpdateWebPageUrl = URLDecoder.decode(userFriendlyUpdateWebPageEncodedUrl,
                    "UTF-8"); //NON-NLS

            return UpdateInfo.create(userFriendlyFullAppName, new URL(userFriendlyUpdateWebPageUrl));
        } catch (ApplicationException e) {
            //noinspection DuplicateStringLiteralInspection
            throw new UpdateException("Error during parsing update info.", e); //NON-NLS
        } catch (UnsupportedEncodingException e) {
            //noinspection DuplicateStringLiteralInspection
            throw new UpdateException("Error during parsing update info.", e); //NON-NLS
        } catch (MalformedURLException e) {
            //noinspection DuplicateStringLiteralInspection
            throw new UpdateException("Error during parsing update info.", e); //NON-NLS
        }
    }
}
