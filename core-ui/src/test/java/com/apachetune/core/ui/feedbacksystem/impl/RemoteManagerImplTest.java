package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.RemoteAbstractTest;
import com.apachetune.core.ui.feedbacksystem.RemoteManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class RemoteManagerImplTest extends RemoteAbstractTest {
    private AppManager mockAppManager;

    @Before
    public void prepare_test() {
        mockAppManager = getMockCtx().mock(AppManager.class);

        getMockCtx().checking(new Expectations() {{
            one(mockAppManager).getFullAppName();
            will(returnValue("test-version"));

            one(mockAppManager).getAppInstallationUid();
            will(returnValue(UUID.fromString("eb14fdd3-1ff0-495b-b94f-93fb0fdb327d")));
        }});
    }

    @Test
    public void test_send_user_feedback() throws Exception {
        setRequestHandler(new AssertQueryAndBodyForPostRequest_Handler("/services/reports?action=send-user-feedback",
                RemoteManagerImplTest.class, "fake_user_feedback_request.xml"));

        RemoteManager testSubj = new RemoteManagerImpl("http://localhost:8181/services/reports", mockAppManager);

        testSubj.sendUserFeedback("progmonster@gmail.com", "user_fake_message");

        assertThat(isRequestHandlerCalled()).isTrue();
    }

    @Test
    public void test_send_user_feedback_without_email_specified() throws Exception {
        setRequestHandler(new AssertQueryAndBodyForPostRequest_Handler("/services/reports?action=send-user-feedback",
                RemoteManagerImplTest.class, "fake_user_feedback_request_without_email_specified.xml"));

        RemoteManager testSubj = new RemoteManagerImpl("http://localhost:8181/services/reports", mockAppManager);

        testSubj.sendUserFeedback(null, "user_fake_message");

        assertThat(isRequestHandlerCalled()).isTrue();
    }
}
