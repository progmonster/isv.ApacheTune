package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.apachetune.httpserver.Constants.REMOTE_MESSAGE_SERVICE_URL_PROP_NAME;
import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;

/**
 * FIXDOC
 */
public class RemoteManagerImpl implements RemoteManager {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImpl.class);

    private final String remoteServiceUrl;

    @Inject
    public RemoteManagerImpl(@Named(REMOTE_MESSAGE_SERVICE_URL_PROP_NAME) String remoteServiceUrl) {
        this.remoteServiceUrl = remoteServiceUrl;
    }

    @Override
    public final List<NewsMessage> loadNewMessages(MessageTimestamp timestamp) {
        HttpClient client = new HttpClient();

        HttpMethod method = new GetMethod(remoteServiceUrl);

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        String strTimeStamp = !timestamp.isEmpty() ? "" + timestamp.getValue() : "";

        String appFullName = "apachetune-2.3"; // todo

        method.setQueryString(format("action=get-news-messages&tstmp={0}&app-fullname={1}", strTimeStamp, appFullName));

        List<NewsMessage> resultList = emptyList();

        try {
            client.executeMethod(method);

            String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");

            resultList = parseResponse(response);                        
        } catch (IOException e) {
            logger.error("Error getting news messages from remote", e);
        }

        method.releaseConnection();
        
        return resultList;
    }

    private List<NewsMessage> parseResponse(String response) {
        List<NewsMessage> resultList = new ArrayList<NewsMessage>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new StringReader(response));

            Document doc = db.parse(is);

            Element docElem = doc.getDocumentElement();

            NodeList messageElems = docElem.getElementsByTagName("message");

            for (int msgIdx = 0; msgIdx < messageElems.getLength(); msgIdx++) {
                NewsMessage msg = parseMessage((Element) messageElems.item(msgIdx));

                resultList.add(msg);
            }
        } catch (ParserConfigurationException e) {
            logger.error("Error during parsing news messages from remote", e);
        } catch (SAXException e) {
            logger.error("Error during parsing news messages from remote", e);
        } catch (IOException e) {
            logger.error("Error during parsing news messages from remote", e);
        } catch (RemoteManagerImplException e) {
            logger.error("Error during parsing news messages from remote", e);
        }

        return resultList;
    }

    private NewsMessage parseMessage(Element msgElem) throws RemoteManagerImplException {
        String dataEncoding = msgElem.getAttribute("dataEncoding");

        if (!dataEncoding.equals("Base64")) {
            throw new RemoteManagerImplException(
                    "Message should be encoded in Base64 encoding [dataEncoding=" + dataEncoding + ']');
        }

        String dataMimeType = msgElem.getAttribute("dataMimeType");

        if (!dataMimeType.equals("Base64")) {
            throw new RemoteManagerImplException(
                    "Message mime/type should be text/html [dataMimeType=" + dataMimeType + ']');
        }

        String strTimestamp = msgElem.getAttribute("tstmp");

        long timestamp;

        try {
            timestamp = Long.parseLong(strTimestamp);
        } catch (NumberFormatException e) {
            throw new RemoteManagerImplException("Message timestamp should be a number [tstmp=" + strTimestamp + ']');
        }

        // todo
        //content
        //subject

        return null; // TODO implement
    }
}
