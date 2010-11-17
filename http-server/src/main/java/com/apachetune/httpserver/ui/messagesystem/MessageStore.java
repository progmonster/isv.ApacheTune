package com.apachetune.httpserver.ui.messagesystem;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * FIXDOC
 */
public interface MessageStore {
    void initialize() throws SQLException;

    void dispose() throws SQLException;

    MessageTimestamp getLastTimestamp();

    List<NewsMessage> getMessages();

    List<NewsMessage> getUnreadMessages();

    void storeMessages(Collection<NewsMessage> messages);

    void deleteMessages(Collection<NewsMessage> messages);

    void deleteAllMessages();

    void addDataChangedListener(MessageStoreDataChangedListener listener);

    void removeDataChangedListener(MessageStoreDataChangedListener listener);
}
