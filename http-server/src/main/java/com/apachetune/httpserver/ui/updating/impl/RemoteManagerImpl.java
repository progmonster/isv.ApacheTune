package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.AppManager;
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

import java.io.IOException;

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
        return null; // TODO implement
    }
}
