package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * FIXDOC
 */
public class LocalMessageStoreImpl implements MessageStore {
    @Override
    public final MessageTimestamp getLastTimestamp() {
        return MessageTimestamp.createEmpty();  // TODO implement
    }

    @Override
    public final List<NewsMessage> getMessages() {
        return emptyList();  // TODO implement
    }

    @Override
    public List<NewsMessage> getUnreadMessages() {
        return emptyList();  // TODO implement
    }

    @Override
    public void storeMessages(List<NewsMessage> expMsg) {
        // TODO implement
    }

    @Override
    public final void deleteMessages(List<NewsMessage> messages) {
        // TODO implement
    }
}
