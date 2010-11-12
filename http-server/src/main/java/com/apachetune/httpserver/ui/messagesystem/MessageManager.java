package com.apachetune.httpserver.ui.messagesystem;

import com.sun.xml.internal.fastinfoset.util.KeyIntMap;
import com.sun.xml.internal.ws.api.message.Header;

import java.util.List;

/**
 * FIXDOC
 */
public interface MessageManager {
    void initialize();

    void dispose();

    void start();

    void stop();

    MessageTimestamp getLastLoadedMessageTimestamp();

    List<NewsMessage> getMessages();

    List<NewsMessage> getUnreadMessages();

    NewsMessage markMessageAsRead(NewsMessage msg);

    NewsMessage markMessageAsUnread(NewsMessage msg);

    void deleteMessage(NewsMessage msg);
}
