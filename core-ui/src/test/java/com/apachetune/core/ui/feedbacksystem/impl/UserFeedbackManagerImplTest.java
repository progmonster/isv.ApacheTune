package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ui.UIWorkItem;
import com.apachetune.core.ui.feedbacksystem.*;
import com.apachetune.events.SendErrorReportEvent;
import com.apachetune.feedbacksystem.FeedbackManager;
import com.google.inject.Provider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;

import static com.apachetune.core.Constants.ON_SEND_ERROR_REPORT_EVENT;
import static com.apachetune.core.ui.feedbacksystem.UserFeedbackView.Result.USER_ACCEPTED_SENDING;
import static com.apachetune.core.ui.feedbacksystem.UserFeedbackView.Result.USER_REJECTED_SENDING;
import static javax.swing.JOptionPane.OK_OPTION;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class UserFeedbackManagerImplTest {
    private final Mockery mockCtx = new JUnit4Mockery();

    private UIWorkItem mockWorkItem;

    private FeedbackManager mockFeedbackManager;

    private UserFeedbackView mockUserFeedbackView;

    private RemoteManager mockRemoteManager;

    private SendUserFeedbackMessageDialog mockSendUserFeedbackMessageDialog;

    private Sequence workflow;

    private UserFeedbackManager testSubj;

    @Before
    public void prepare_test() {
        mockWorkItem = mockCtx.mock(UIWorkItem.class);

        mockFeedbackManager = mockCtx.mock(FeedbackManager.class);

        mockUserFeedbackView = mockCtx.mock(UserFeedbackView.class);

        Provider<UserFeedbackView> fakeUserFeedbackViewProvider = new Provider<UserFeedbackView>() {
            @Override
            public final UserFeedbackView get() {
                return mockUserFeedbackView;
            }
        };

        mockRemoteManager = mockCtx.mock(RemoteManager.class);

        mockSendUserFeedbackMessageDialog = mockCtx.mock(SendUserFeedbackMessageDialog.class);

        workflow = mockCtx.sequence("workflow");

        testSubj = new UserFeedbackManagerImpl(mockWorkItem, fakeUserFeedbackViewProvider, mockRemoteManager,
                mockFeedbackManager, mockSendUserFeedbackMessageDialog, null);
    }
    
    @Test
    public void test_user_cancel_sending_feedback() throws Exception {
        mockCtx.checking(new Expectations(){{
            allowing(mockFeedbackManager).getUserEmail();
            will(returnValue("progmonster@gmail.com"));

            oneOf(mockUserFeedbackView).initialize(mockWorkItem);
            inSequence(workflow);

            oneOf(mockUserFeedbackView).setUserEmail("progmonster@gmail.com");
            inSequence(workflow);

            oneOf(mockUserFeedbackView).run();
            inSequence(workflow);

            oneOf(mockUserFeedbackView).getResult();
            inSequence(workflow);
            will(returnValue(USER_REJECTED_SENDING));
        }});

        testSubj.sendUserFeedback();
    }

    @Test
    public void test_send_user_feedback() throws Exception {
        mockCtx.checking(new Expectations(){{
            allowing(mockFeedbackManager).getUserEmail();
            will(returnValue("progmonster@gmail.com"));

            one(mockFeedbackManager).storeUserEMail("progmonster@gmail.com");

            oneOf(mockUserFeedbackView).initialize(mockWorkItem);
            inSequence(workflow);

            oneOf(mockUserFeedbackView).setUserEmail("progmonster@gmail.com");
            inSequence(workflow);

            oneOf(mockUserFeedbackView).run();
            inSequence(workflow);

            oneOf(mockUserFeedbackView).getResult();
            inSequence(workflow);
            will(returnValue(USER_ACCEPTED_SENDING));

            oneOf(mockUserFeedbackView).getUserEmail();
            inSequence(workflow);
            will(returnValue("progmonster@gmail.com"));

            oneOf(mockUserFeedbackView).getUserMessage();
            inSequence(workflow);
            will(returnValue("fake_user_message"));

            oneOf(mockUserFeedbackView).dispose();
            inSequence(workflow);

            oneOf(mockRemoteManager).sendUserFeedback("progmonster@gmail.com", "fake_user_message");
            inSequence(workflow);

            oneOf(mockSendUserFeedbackMessageDialog).showSuccess();
            inSequence(workflow);
        }});

        testSubj.sendUserFeedback();
    }

    @Test
    public void test_fail_on_user_feedback_sending() throws Exception {
        mockCtx.checking(new Expectations(){{
            allowing(mockFeedbackManager).getUserEmail();
            will(returnValue("progmonster@gmail.com"));

            one(mockFeedbackManager).storeUserEMail("progmonster@gmail.com");

            oneOf(mockUserFeedbackView).initialize(mockWorkItem);
            inSequence(workflow);

            oneOf(mockUserFeedbackView).setUserEmail("progmonster@gmail.com");
            inSequence(workflow);

            oneOf(mockUserFeedbackView).run();
            inSequence(workflow);

            oneOf(mockUserFeedbackView).getResult();
            inSequence(workflow);
            will(returnValue(USER_ACCEPTED_SENDING));

            oneOf(mockUserFeedbackView).getUserEmail();
            inSequence(workflow);
            will(returnValue("progmonster@gmail.com"));

            oneOf(mockUserFeedbackView).getUserMessage();
            inSequence(workflow);
            will(returnValue("fake_user_message"));

            oneOf(mockUserFeedbackView).dispose();
            inSequence(workflow);

            oneOf(mockRemoteManager).sendUserFeedback("progmonster@gmail.com", "fake_user_message");
            inSequence(workflow);
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new RemoteException("fake_exception")));

            //noinspection ThrowableResultOfMethodCallIgnored
            oneOf(mockSendUserFeedbackMessageDialog).showError(with(any(RemoteException.class)));
            inSequence(workflow);
            will(returnValue(OK_OPTION));

            oneOf(mockWorkItem).raiseEvent(with(ON_SEND_ERROR_REPORT_EVENT), with(any(SendErrorReportEvent.class)));
            inSequence(workflow);
        }});

        testSubj.sendUserFeedback();
    }
}
