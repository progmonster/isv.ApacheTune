package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.AppManager;
import com.apachetune.httpserver.RemoteAbstractTest;
import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class RemoteManagerImplTest extends RemoteAbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(RemoteManagerImplTest.class);

    private RemoteManager testSubject;

    private AppManager mockAppManager;

    @Before
    public void prepare_test() throws Exception {
        mockAppManager = getMockCtx().mock(AppManager.class);

        getMockCtx().checking(new Expectations() {{
            allowing(mockAppManager).getFullAppName();
            will(returnValue("apache-tune-test-version"));
        }});
        
        testSubject = new RemoteManagerImpl(testRemoteServiceUrl, mockAppManager);
    }

    @Test
    public void test_load_new_messages() throws Exception {
        setRequestHandler(new AssertQueryAndLoadResponseFromResource_Handler(
                "action=get-news-messages&tstmp=2&app-fullname=apache-tune-test-version",
                RemoteManagerImplTest.class,
                "fake_news_messages_response_1.xml"
        ));

        List<NewsMessage> loadedMessages = testSubject.loadNewMessages(MessageTimestamp.create(2));

        assertThat(loadedMessages.size()).isEqualTo(0);
    }

    @Test
    public void test_load_new_messages_first_time() throws Exception {
        final NewsMessage expMsg1 = new NewsMessage(MessageTimestamp.create(1), "subject1", "content1", true);
                                                                                                      
        final NewsMessage expMsg2 = new NewsMessage(MessageTimestamp.create(2), "subject2", "content2", true);

        setRequestHandler(new AssertQueryAndLoadResponseFromResource_Handler(
                "action=get-news-messages&tstmp=&app-fullname=apache-tune-test-version",
                RemoteManagerImplTest.class,
                "fake_news_messages_response_2.xml"
        ));

        RemoteManager testSubject = new RemoteManagerImpl(testRemoteServiceUrl, mockAppManager);

        List<NewsMessage> loadedMessages = testSubject.loadNewMessages(MessageTimestamp.createEmpty());

        assertThat(loadedMessages.size()).isEqualTo(2);

        assertThat(loadedMessages.get(0)).isEqualTo(expMsg1);
        assertThat(loadedMessages.get(1)).isEqualTo(expMsg2);
    }

}
