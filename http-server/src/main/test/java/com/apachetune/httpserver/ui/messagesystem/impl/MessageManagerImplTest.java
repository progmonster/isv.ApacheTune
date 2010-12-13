package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.messagesystem.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class MessageManagerImplTest {
    private final Mockery mockCtx = new JUnit4Mockery();

    private StatusBarManager mockStatusBarManager;

    private MessageStatusBarSite mockMessageStatusBarSite;

    private MessageStore mockMessageStore;

    private RemoteManager mockRemoteManager;

    private MessageManager testSubj;

    private Scheduler mockScheduler;

    @Before
    public void prepare_test() throws Exception {
        mockStatusBarManager = mockCtx.mock(StatusBarManager.class);

        mockMessageStatusBarSite = mockCtx.mock(MessageStatusBarSite.class);

        mockMessageStore = mockCtx.mock(MessageStore.class);

        mockRemoteManager = mockCtx.mock(RemoteManager.class);

        mockScheduler = mockCtx.mock(Scheduler.class);

        ScheduleLoadNewsMessagesStrategy fakeScheduleLoadNewsMessagesStrategy = new ScheduleLoadNewsMessagesStrategy() {
            @Override
            public final void scheduleLoadNewsMessages(Runnable loadNewsMessagesTask) {
                loadNewsMessagesTask.run();
            }
        };

        testSubj = new MessageManagerImpl(mockStatusBarManager, mockMessageStatusBarSite, mockMessageStore,
                mockRemoteManager, fakeScheduleLoadNewsMessagesStrategy);

        mockCtx.checking(new Expectations() {{
            ignoring(mockStatusBarManager).addStatusBarSite(mockMessageStatusBarSite);

            ignoring(mockMessageStore).initialize();
            ignoring(mockMessageStatusBarSite).initialize();
            ignoring(mockMessageStatusBarSite).dispose();

            ignoring(mockMessageStore).addDataChangedListener(with(any(MessageStoreDataChangedListener.class)));
            ignoring(mockMessageStore).removeDataChangedListener(with(any(MessageStoreDataChangedListener.class)));
        }});

        testSubj.initialize();
    }

    @After
    public void dispose_test_subject() throws Exception {
        mockCtx.checking(new Expectations() {{
            ignoring(mockStatusBarManager).removeStatusBarSite(mockMessageStatusBarSite);

            ignoring(mockMessageStore).dispose();
        }});

        testSubj.dispose();
    }

    @Test
    public void test_get_last_loaded_message_timestamp() throws Exception {
        mockCtx.checking(new Expectations() {{
            allowing(mockMessageStore).getLastTimestamp();
            will(returnValue(MessageTimestamp.create(123)));
        }});

        assertThat(testSubj.getLastLoadedMessageTimestamp()).isEqualTo(MessageTimestamp.create(123));
    }

    @Test
    public void test_lists() throws Exception {
        final NewsMessage expMsg1 = new NewsMessage(MessageTimestamp.create(1), "expMsg1", "expMsg1 content", true);

        final NewsMessage expMsg2 = new NewsMessage(MessageTimestamp.create(1), "expMsg2", "expMsg2 content", false);

        mockCtx.checking(new Expectations() {{
            allowing(mockMessageStore).getUnreadMessages();
            will(returnValue(asList(expMsg1)));

            allowing(mockMessageStore).getMessages();
            will(returnValue(asList(expMsg1, expMsg2)));
        }});

        List<NewsMessage> unreadMessages = testSubj.getUnreadMessages();

        assertThat(unreadMessages.size()).isEqualTo(1);

        assertThat(unreadMessages.get(0)).isEqualTo(expMsg1);

        List<NewsMessage> allMessages = testSubj.getMessages();

        assertThat(allMessages.size()).isEqualTo(2);

        assertThat(allMessages.get(0)).isEqualTo(expMsg1);
        assertThat(allMessages.get(1)).isEqualTo(expMsg2);
    }

    @Test
    public void test_load_new_messages_on_start() throws Exception {
        final NewsMessage expMsg = new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", true);

        final Sequence sequence = mockCtx.sequence("flow");

        final States state = mockCtx.states("test_state").startsAs("initial_state");

        mockCtx.checking(new Expectations() {{
            one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));

            allowing(mockMessageStore).getLastTimestamp();
            when(state.is("initial_state"));
            will(returnValue(MessageTimestamp.createEmpty()));

            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("initial_state"));
            will(returnValue(emptyList()));

            allowing(mockMessageStatusBarSite).setNotificationAreaActive(false);
            when(state.is("initial_state"));

            allowing(mockMessageStatusBarSite).setNotificationTip("There are no unread messages.");
            when(state.is("initial_state"));

            one(mockRemoteManager).loadNewMessages(MessageTimestamp.createEmpty());
            inSequence(sequence);
            will(returnValue(asList(expMsg)));

            one(mockMessageStore).storeMessages(asList(expMsg));
            inSequence(sequence);
            then(state.is("new_messages_loaded_and_stored"));

            one(mockMessageStatusBarSite).setNotificationAreaActive(true);
            when(state.is("new_messages_loaded_and_stored"));

            one(mockMessageStatusBarSite).setNotificationTip(with(any(String.class)));
            when(state.is("new_messages_loaded_and_stored"));

            one(mockMessageStatusBarSite).showBalloonTip(with(any(String.class)));
            when(state.is("new_messages_loaded_and_stored"));

            allowing(mockMessageStore).getLastTimestamp();
            when(state.is("new_messages_loaded_and_stored"));
            will(returnValue(expMsg.getTimestamp()));

            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("new_messages_loaded_and_stored"));
            will(returnValue(asList(expMsg)));
        }});

        testSubj.start();

        assertThat(testSubj.getLastLoadedMessageTimestamp()).isEqualTo(expMsg.getTimestamp());

        assertThat(testSubj.getUnreadMessages().size()).isEqualTo(1);

        assertThat(testSubj.getUnreadMessages().get(0)).isEqualTo(expMsg);
    }

    @Test
    public void test_mark_message_as_read() throws Exception {
        final NewsMessage expMsg = new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", true);

        final States state = mockCtx.states("test_state").startsAs("initial_state");

        mockCtx.checking(new Expectations() {{
            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("initial_state"));
            will(returnValue(asList(expMsg)));

            one(mockMessageStore).storeMessages(
                    asList(new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", false)));
            then(state.is("marked_message_as_read"));

            one(mockMessageStatusBarSite).setNotificationAreaActive(false);

            one(mockMessageStatusBarSite).setNotificationTip(with(aNull(String.class)));

            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("marked_message_as_read"));
            will(returnValue(emptyList()));
        }});

        List<NewsMessage> messages = testSubj.getUnreadMessages();

        assertThat(messages.size()).isEqualTo(1);

        NewsMessage msg = messages.get(0);

        NewsMessage readMsg = testSubj.markMessageAsRead(msg);

        assertThat(readMsg.isUnread()).isFalse();
    }

    @Test
    public void test_mark_message_as_unread() throws Exception {
        final NewsMessage expMsg = new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", true);

        final States state = mockCtx.states("test_state").startsAs("initial_state");

        mockCtx.checking(new Expectations() {{
            allowing(mockMessageStore).getMessages();
            when(state.is("initial_state"));
            will(returnValue(asList(new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", false))));

            one(mockMessageStore).storeMessages(asList(expMsg));
            then(state.is("marked_message_as_unread"));

            one(mockMessageStatusBarSite).setNotificationAreaActive(true);

            one(mockMessageStatusBarSite).setNotificationTip(with(aNonNull(String.class)));

            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("marked_message_as_unread"));
            will(returnValue(asList(expMsg)));
        }});

        List<NewsMessage> messages = testSubj.getMessages();

        assertThat(messages.size()).isEqualTo(1);

        NewsMessage msg = messages.get(0);

        NewsMessage unreadMsg = testSubj.markMessageAsUnread(msg);

        assertThat(unreadMsg.isUnread()).isTrue();
    }

    @Test
    public void test_delete_message() throws Exception {
        final NewsMessage expMsg = new NewsMessage(MessageTimestamp.create(1), "expMsg", "expMsg content", true);

        final States state = mockCtx.states("test_state").startsAs("initial_state");

        mockCtx.checking(new Expectations() {{
            allowing(mockMessageStore).getMessages();
            when(state.is("initial_state"));
            will(returnValue(asList(expMsg)));

            one(mockMessageStore).deleteMessages(asList(expMsg));
            then(state.is("message_deleted"));

            one(mockMessageStatusBarSite).setNotificationAreaActive(false);

            one(mockMessageStatusBarSite).setNotificationTip(with(aNull(String.class)));

            allowing(mockMessageStore).getUnreadMessages();
            when(state.is("message_deleted"));
            will(returnValue(emptyList()));
        }});

        List<NewsMessage> messages = testSubj.getMessages();

        assertThat(messages.size()).isEqualTo(1);

        NewsMessage msg = messages.get(0);

        assertThat(msg.isUnread()).isTrue();

        testSubj.deleteMessage(msg);
    }
}
