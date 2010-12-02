package com.apachetune.feedbacksystem.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.feedbacksystem.FeedbackManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.apachetune.core.Constants.REMOTE_SERVICE_USER_EMAIL_PROP_NAME;
import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class FeedbackManagerImplTest {
    private final Mockery mockCtx = new JUnit4Mockery();

    private PreferencesManager mockPreferencesManager;

    private Preferences mockPrefs;

    @Before
    public void prepare_test() {
        mockPreferencesManager = mockCtx.mock(PreferencesManager.class);

        mockPrefs = mockCtx.mock(Preferences.class);

        mockCtx.checking(new Expectations() {{
            allowing(mockPreferencesManager).userNodeForPackage(FeedbackManagerImpl.class);
            will(returnValue(mockPrefs));
        }});
    }

    @Test
    public void test_store_and_get_user_email() throws Exception {
        mockCtx.checking(new Expectations() {{
            one(mockPrefs).put(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, "progmonster@gmail.com");
            one(mockPrefs).flush();

            allowing(mockPrefs).get(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, null);
            will(returnValue("progmonster@gmail.com"));
        }});

        FeedbackManager testSubject = new FeedbackManagerImpl(mockPreferencesManager);

        testSubject.storeUserEMail("progmonster@gmail.com");

        assertThat(testSubject.getUserEmail()).isEqualTo("progmonster@gmail.com");
    }

    @Test
    public void test_store_and_get_user_null_email() throws Exception {
        mockCtx.checking(new Expectations() {{
            one(mockPrefs).remove(REMOTE_SERVICE_USER_EMAIL_PROP_NAME);
            one(mockPrefs).flush();

            allowing(mockPrefs).get(REMOTE_SERVICE_USER_EMAIL_PROP_NAME, null);
            will(returnValue(null));
        }});

        FeedbackManager testSubject = new FeedbackManagerImpl(mockPreferencesManager);

        testSubject.storeUserEMail(null);

        assertThat(testSubject.getUserEmail()).isNull();
    }

    @Ignore
    @Test
    public void test_send_app_log() throws Exception {
        // TODO проверить отправку всех файлов логов и их последущего удаления.
    }

    @Ignore
    @Test
    public void test_send_error_report() throws Exception {
        // TODO проверить отправку сообщения об возникшем исключении.
    }
}
