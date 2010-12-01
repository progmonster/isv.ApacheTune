package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.RemoteAbstractTest;
import com.apachetune.core.ui.feedbacksystem.RemoteManager;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
public class RemoteManagerImplTest extends RemoteAbstractTest {
    @Test
    public void test_send_user_feedback() {
        setRequestHandler(new AssertQueryAndBodyForPostRequest_Handler("services/reports?action=send-user-feedback", RemoteManagerImplTest.class, "fake_user_feedback_request.xml"));

        RemoteManager testSubj = new RemoteManagerImpl();

        testSubj.sendUserFeedback("progmonster@gmail.com", "user_fake_message");

        assertThat(isRequestHandlerCalled()).isTrue();
    }

    @Test
    public void test_send_user_feedback_without_email_specified() {
        RemoteManager testSubj = new RemoteManagerImpl();

        testSubj.sendUserFeedback(null, "user_fake_message");

        assertThat(isRequestHandlerCalled()).isTrue();
    }
}
