package com.apachetune.httpserver.ui.messagesystem;

import java.util.List;

/**
 * FIXDOC
 */
public interface RemoteManager {
    List<NewsMessage> loadNewMessages(MessageTimestamp timestamp);
}
