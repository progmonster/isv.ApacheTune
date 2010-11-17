package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import com.apachetune.core.ui.NView;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;

import java.util.List;

/**
 * FIXDOC
 */
public interface MessageView extends NView {
    List<NewsMessage> getSelectedMessages();

    void unselectAllMessages();

    void selectAllMessages();

    NewsMessage getCurrentMessage();

    void notifyDataChanged();

    void setMessageControlsEnabled(boolean enabled);
}
