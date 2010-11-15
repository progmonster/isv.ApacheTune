package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.AppManager;
import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.codec.binary.Base64;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.apachetune.httpserver.Constants.REMOTE_MESSAGE_SERVICE_URL_PROP_NAME;
import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;

/**
 * FIXDOC
 */
public class RemoteManagerImpl implements RemoteManager {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImpl.class);

    private final String remoteServiceUrl;

    private final AppManager appManager;

    @Inject
    public RemoteManagerImpl(@Named(REMOTE_MESSAGE_SERVICE_URL_PROP_NAME) String remoteServiceUrl,
                             AppManager appManager) {
        this.remoteServiceUrl = remoteServiceUrl;
        this.appManager = appManager;
    }

    @Override
    public final List<NewsMessage> loadNewMessages(MessageTimestamp timestamp) {
        HttpClient client = new HttpClient();

        HttpMethod method = new GetMethod(remoteServiceUrl);

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        String strTimeStamp = !timestamp.isEmpty() ? "" + timestamp.getValue() : "";

        method.setQueryString(format("action=get-news-messages&tstmp={0}&app-fullname={1}", strTimeStamp,
                appManager.getFullAppName()));

        List<NewsMessage> resultList = emptyList();

        int resultCode;

        try {
            resultCode = client.executeMethod(method);

            if (resultCode == SC_OK) {
                String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");

                resultList = parseResponse(response);
            } else {
                logger.error("Remote news message service returned error response code [code=" + resultCode + ']');
            }
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
        } catch (Throwable cause) {
            logger.error("Error during parsing news messages from remote", cause);
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

        if (!dataMimeType.equals("text/html")) {
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

        if (timestamp <= 0) {
            throw new RemoteManagerImplException(
                    "Message timestamp should be a non null positive value [tstmp=" + timestamp + ']');
        }

        String base64Subject = getChildElementContent(msgElem, "subject");

        String subject;

        try {
            subject = new String(Base64.decodeBase64(base64Subject), "UTF-8");
        } catch (Throwable cause) {
            throw new RemoteManagerImplException(
                    "Error during parsing message subject [unparsed_subject=" + base64Subject + ']', cause);
        }

        String base64Content = getChildElementContent(msgElem, "content");

        String content;

        try {
            content = new String(Base64.decodeBase64(base64Content), "UTF-8");
        } catch (Throwable cause) {
            throw new RemoteManagerImplException(
                    "Error during parsing message content [unparsed_content=" + base64Content + ']', cause);
        }

        NewsMessage msg = new NewsMessage(MessageTimestamp.create(timestamp), subject, content, true);

        return msg;
    }

    private String getChildElementContent(Element element, String childElementName) throws RemoteManagerImplException {
        NodeList childElems = element.getElementsByTagName(childElementName);

        if (childElems.getLength() != 1) {
            throw new RemoteManagerImplException(
                    "Error during parsing message. Multiple children elements with same name. [child_element_name=" +
                            childElementName + ']');
        }

        String result;

        try {
            Element childElem = (Element) childElems.item(0);

            result = childElem.getTextContent().trim();
        } catch (Throwable cause) {
            throw new RemoteManagerImplException("Error during parsing message", cause);
        }

        return result;
    }
}
