package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.core.AppManager;
import com.apachetune.httpserver.RemoteAbstractTest;
import com.apachetune.httpserver.ui.updating.RemoteManager;
import com.apachetune.httpserver.ui.updating.UpdateException;
import com.apachetune.httpserver.ui.updating.UpdateInfo;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.URL;

import static com.apachetune.httpserver.ui.updating.UpdateInfo.createNoUpdateInfo;
import static org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST;
import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class RemoteManagerImplTest extends RemoteAbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImplTest.class);

    private AppManager mockAppManager;

    private RemoteManager testSubj;

    @Before
    public void prepare_test() {
        mockAppManager = getMockCtx().mock(AppManager.class);

        getMockCtx().checking(new Expectations() {{
            allowing(mockAppManager).getFullAppName();
            will(returnValue("apache-tune-test-version"));
        }});

        testSubj = new RemoteManagerImpl(testRemoteServiceUrl, mockAppManager);
    }

    @Test
    public void test_check_has_update() throws Exception {
        setRequestHandler(new AssertQueryAndLoadResponseFromResource_Handler(
                "action=check-for-updates&app-fullname=apache-tune-test-version",
                RemoteManagerImplTest.class,
                "fake_has_update_response.xml"
        ));

        UpdateInfo expUpdateInfo = UpdateInfo.create("ApacheTune 1.2 Light", new URL("http://apachetune.com/update"));

        UpdateInfo updateInfo = testSubj.checkUpdateAvailable();

        assertThat(updateInfo).isEqualTo(expUpdateInfo);
    }

    @Test
    public void test_check_has_no_update() throws Exception {
        setRequestHandler(new AssertQueryAndLoadResponseFromResource_Handler(
                "action=check-for-updates&app-fullname=apache-tune-test-version",
                RemoteManagerImplTest.class,
                "fake_has_no_update_response.xml"
        ));

        UpdateInfo updateInfo = testSubj.checkUpdateAvailable();

        assertThat(updateInfo).isEqualTo(createNoUpdateInfo());
    }

    @Test(expected = UpdateException.class)
    public void test_should_throws_exception_on_communication_error() throws Exception {
        getTestServiceConnection().close();

        testSubj.checkUpdateAvailable();
    }

    @Test(expected = UpdateException.class)
    public void test_should_throws_exception_on_invalid_response() throws Exception {
        setRequestHandler(new Container() {
            @Override
            public final void handle(Request request, Response response) {
                PrintStream body = null;

                try {
                    body = response.getPrintStream();

                    response.set("Content-Type", "text/xml");

                    body.println("invalid-fake-response-body");
                } catch (Throwable cause) {
                    logger.error("Request handling error", cause);
                } finally {
                    if (body != null) {
                        body.close();
                    }
                }
            }
        });

        testSubj.checkUpdateAvailable();
    }

    @Test(expected = UpdateException.class)
    public void test_should_throws_exception_on_response_with_non_ok_status() throws Exception {
        setRequestHandler(new Container() {
            @Override
            public final void handle(Request request, Response response) {
                try {
                    response.setCode(SC_BAD_REQUEST);

                    response.close();
                } catch (Throwable cause) {
                    logger.error("Request handling error", cause);
                }
            }
        });

        testSubj.checkUpdateAvailable();
    }
}
