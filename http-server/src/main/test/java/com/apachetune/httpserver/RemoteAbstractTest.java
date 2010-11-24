package com.apachetune.httpserver;

import org.apache.commons.io.IOUtils;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.InetSocketAddress;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
public abstract class RemoteAbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(RemoteAbstractTest.class);

    protected static final String testRemoteServiceUrl = "http://localhost:8181/";

    private final Mockery mockCtx = new JUnit4Mockery();

    private Connection connection;

    private HttpService httpService;

    @Before
    public void prepare_remote_test() throws Exception {
        httpService = new HttpService();

        connection = new SocketConnection(httpService);

        InetSocketAddress address = new InetSocketAddress(8181);

        connection.connect(address);        
    }

    @After
    public void tear_down_test() throws Exception {
        connection.close();

        httpService.setHandler(null);
    }

    protected final void setRequestHandler(Container handler) {
        httpService.setHandler(handler);
    }

    protected final Connection getTestServiceConnection() {
        return connection;
    }

    protected final Mockery getMockCtx() {
        return mockCtx;
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

    protected class AssertQueryAndLoadResponseFromResource_Handler implements Container {
        private final String expectationQuery;

        private final Class clazz;

        private final String resourceName;

        public AssertQueryAndLoadResponseFromResource_Handler(String expectationQuery, Class clazz,
                                                             String resourceName) {
            this.expectationQuery = expectationQuery;
            this.clazz = clazz;
            this.resourceName = resourceName;
        }

        @Override
        public final void handle(Request request, Response response) {
            PrintStream body = null;

            try {
                assertThat(request.getQuery().toString()).isEqualTo(expectationQuery);

                body = response.getPrintStream();

                response.set("Content-Type", "text/xml");

                String fakeResp = IOUtils.toString(clazz.getResourceAsStream(resourceName), "UTF-8");

                body.println(fakeResp);
            } catch (Throwable cause) {
                logger.error("Request handling error", cause);
            } finally {
                if (body != null) {
                    body.close();
                }
            }
        }
    }
}
