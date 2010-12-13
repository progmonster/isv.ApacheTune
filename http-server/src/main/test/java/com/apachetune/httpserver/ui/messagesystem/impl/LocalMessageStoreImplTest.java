package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
public class LocalMessageStoreImplTest{
    private static final String TEST_DB_URL = "jdbc:h2:mem:test_message_db";

    private LocalMessageStoreImpl testSubj;

    @Before
    public void clean_test_db() throws Exception {
        testSubj.deleteAllMessages();
    }

    @Before
    public void prepare_test_subject() throws Exception {
        testSubj = new LocalMessageStoreImpl(TEST_DB_URL);

        testSubj.initialize();
    }

    @After
    public void dispose_test_subject() throws Exception {
        testSubj.dispose();
    }

    @Test
    public void test_store_and_get_messages() throws Exception {
        NewsMessage msg1 = new NewsMessage(MessageTimestamp.create(1), "msg1", "msg1 content", false);

        NewsMessage msg2 = new NewsMessage(MessageTimestamp.create(2), "msg2", "msg2 content", true);

        assertThat(testSubj.getMessages().size()).isEqualTo(0);
        assertThat(testSubj.getUnreadMessages().size()).isEqualTo(0);

        testSubj.storeMessages(asList(msg1, msg2));

        assertThat(testSubj.getMessages().size()).isEqualTo(2);

        assertThat(testSubj.getUnreadMessages().size()).isEqualTo(1);
        assertThat(testSubj.getUnreadMessages().get(0)).isEqualTo(msg2);

        testSubj.dispose();
    }

    @Test
    public void test_delete_messages() throws Exception {
        NewsMessage msg1 = new NewsMessage(MessageTimestamp.create(1), "msg1", "msg1 content", false);

        NewsMessage msg2 = new NewsMessage(MessageTimestamp.create(2), "msg2", "msg2 content", true);

        NewsMessage msg3 = new NewsMessage(MessageTimestamp.create(3), "msg3", "msg3 content", false);

        testSubj.storeMessages(asList(msg1, msg2, msg3));

        testSubj.deleteMessages(asList(msg2, msg3));

        assertThat(testSubj.getMessages().size()).isEqualTo(1);
        assertThat(testSubj.getMessages().get(0)).isEqualTo(msg1);

        assertThat(testSubj.getUnreadMessages().size()).isEqualTo(0);
    }

    @Test
    public void test_replace_message() throws Exception {
        NewsMessage msg1 = new NewsMessage(MessageTimestamp.create(1), "msg1", "msg1 content", false);

        NewsMessage msg2 = new NewsMessage(MessageTimestamp.create(1), "msg2", "msg2 content", true);

        testSubj.storeMessages(asList(msg1));
        testSubj.storeMessages(asList(msg2));

        assertThat(testSubj.getMessages().size()).isEqualTo(1);
        assertThat(testSubj.getMessages().get(0)).isEqualTo(msg2);

        assertThat(testSubj.getUnreadMessages().size()).isEqualTo(1);
        assertThat(testSubj.getUnreadMessages().get(0)).isEqualTo(msg2);
    }

    @Test
    public void test_get_last_message_timestamp() throws Exception {
        NewsMessage msg1 = new NewsMessage(MessageTimestamp.create(1), "msg1", "msg content1", true);

        NewsMessage msg2 = new NewsMessage(MessageTimestamp.create(2), "msg2", "msg content2", true);

        NewsMessage msg3 = new NewsMessage(MessageTimestamp.create(3), "msg3", "msg content3", true);

        assertThat(testSubj.getLastTimestamp()).isEqualTo(MessageTimestamp.createEmpty());

        testSubj.storeMessages(asList(msg1));

        assertThat(testSubj.getLastTimestamp()).isEqualTo(MessageTimestamp.create(1));

        testSubj.storeMessages(asList(msg3));

        assertThat(testSubj.getLastTimestamp()).isEqualTo(MessageTimestamp.create(3));

        testSubj.storeMessages(asList(msg2));

        assertThat(testSubj.getLastTimestamp()).isEqualTo(MessageTimestamp.create(3));

        testSubj.deleteAllMessages();

        assertThat(testSubj.getLastTimestamp()).isEqualTo(MessageTimestamp.create(3));
    }
}
