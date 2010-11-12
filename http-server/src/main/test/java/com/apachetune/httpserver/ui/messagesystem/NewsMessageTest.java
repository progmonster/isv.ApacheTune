package com.apachetune.httpserver.ui.messagesystem;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 */
public class NewsMessageTest {
    @Test
    public void test_build() {
        NewsMessage expMsg = new NewsMessage(MessageTimestamp.create(12), "fake_title", "fake_content", true);

        NewsMessage.Builder builder = NewsMessage.createBuilder();

        builder.copyFrom(expMsg);

        NewsMessage msg = builder.build();

        assertThat(msg).isEqualTo(expMsg);
    }
}
