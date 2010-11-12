package com.apachetune.httpserver.ui.messagesystem;

import java.util.List;

/**
 * FIXDOC
 */
public interface MessageStore {
    MessageTimestamp getLastTimestamp();

    List<NewsMessage> getMessages();

    List<NewsMessage> getUnreadMessages();

    void storeMessages(List<NewsMessage> messages);

    void deleteMessages(List<NewsMessage> messages);
}
