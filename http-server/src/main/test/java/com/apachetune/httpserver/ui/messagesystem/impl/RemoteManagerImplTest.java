package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
public class RemoteManagerImplTest {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImplTest.class);

    private static final String testRemoteServiceUrl = "http://localhost:8181/";

    private Connection connection;

    private RemoteManager testSubject;

    private HttpService httpService;

    @Before
    public void prepare_test() throws Exception {
        httpService = new HttpService();

        connection = new SocketConnection(httpService);

        InetSocketAddress address = new InetSocketAddress(8181);

        connection.connect(address);

        testSubject = new RemoteManagerImpl(testRemoteServiceUrl);
    }

    @After
    public void tear_down_test() throws Exception {
        connection.close();

        httpService.setHandler(null);
    }

    @Test
    public void test_load_new_messages() throws Exception {
        Container handler = new Container() {
            @Override
            public void handle(Request request, Response response) {
                PrintStream body = null;

                try {
                    body = response.getPrintStream();

                    assertThat(request.getQuery().toString())
                            .isEqualTo("action=get-news-messages&tstmp=2&app-fullname=apachetune-2.3");

                    response.set("Content-Type", "text/xml");

                    String fakeResp = IOUtils.toString(
                            RemoteManagerImplTest.class.getResourceAsStream("fake_news_messages_response_1.xml"),
                            "UTF-8");

                    body.println(fakeResp);
                } catch (Throwable cause) {
                    logger.error("Request handling error", cause);
                } finally {
                    if (body != null) {
                        body.close();                        
                    }
                }
            }
        };

        httpService.setHandler(handler);

        List<NewsMessage> loadedMessages = testSubject.loadNewMessages(MessageTimestamp.create(2));

        assertThat(loadedMessages.size()).isEqualTo(0);
    }

    @Test
    public void test_load_new_messages_first_time() throws Exception {
        Container handler = new Container() {
            @Override
            public void handle(Request request, Response response) {
                PrintStream body = null;

                try {
                    body = response.getPrintStream();

                    assertThat(request.getQuery().toString())
                            .isEqualTo("action=get-news-messages&tstmp=&app-fullname=apachetune-2.3");

                    response.set("Content-Type", "text/xml");

                    String fakeResp = IOUtils.toString(
                            RemoteManagerImplTest.class.getResourceAsStream("fake_news_messages_response_2.xml"),
                            "UTF-8");

                    body.println(fakeResp);
                } catch (Throwable cause) {
                    logger.error("Request handling error", cause);
                } finally {
                    if (body != null) {
                        body.close();
                    }
                }
            }
        };

        httpService.setHandler(handler);

        RemoteManager testSubject = new RemoteManagerImpl(testRemoteServiceUrl);

        List<NewsMessage> loadedMessages = testSubject.loadNewMessages(MessageTimestamp.createEmpty());

        assertThat(loadedMessages.size()).isEqualTo(2);
    }

    private class HttpService implements Container {
        private Container handler;

        public final void setHandler(Container handler) {
            this.handler = handler;
        }

        @Override
        public final void handle(Request req, Response resp) {
            if (handler != null) {
                handler.handle(req, resp);
            } else {
                logger.error("You should set handler up before handling requests.");
            }
        }
    }
}
