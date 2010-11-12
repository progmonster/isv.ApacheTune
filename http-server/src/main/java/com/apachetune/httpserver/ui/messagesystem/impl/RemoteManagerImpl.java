package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageTimestamp;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * FIXDOC
 */
public class RemoteManagerImpl implements RemoteManager {
    @Override
    public final List<NewsMessage> loadNewMessages(MessageTimestamp timestamp) {
        return Collections.emptyList(); // TODO implement
    }
}
