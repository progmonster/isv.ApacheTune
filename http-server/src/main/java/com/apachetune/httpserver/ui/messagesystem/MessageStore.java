package com.apachetune.httpserver.ui.messagesystem;

import java.sql.SQLException;
import java.util.List;

/**
 * FIXDOC
 */
public interface MessageStore {
    void initialize() throws SQLException;

    void dispose() throws SQLException;

    MessageTimestamp getLastTimestamp() throws SQLException;

    List<NewsMessage> getMessages() throws SQLException;

    List<NewsMessage> getUnreadMessages() throws SQLException;

    void storeMessages(List<NewsMessage> messages) throws SQLException;

    void deleteMessages(List<NewsMessage> messages) throws SQLException;

    void deleteAllMessages() throws SQLException;
}
