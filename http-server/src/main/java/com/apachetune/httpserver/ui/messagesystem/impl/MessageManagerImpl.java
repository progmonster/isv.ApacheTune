package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.messagesystem.*;
import com.google.inject.Inject;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * FIXDOC
 */
public class MessageManagerImpl implements MessageManager {
    private final StatusBarManager statusBarManager;

    private final MessageStatusBarSite messageStatusBarSite;

    private final MessageStore messageStore;

    private final RemoteManager remoteManager;

    private static final String HAVE_UNREAD_MESSAGES_MSG_TMPL = "There are {0} unread messages";  // todo localize

    @Inject
    public MessageManagerImpl(StatusBarManager statusBarManager,
                              MessageStatusBarSite messageStatusBarSite,
                              MessageStore messageStore, RemoteManager remoteManager) {
        this.statusBarManager = statusBarManager;
        this.messageStatusBarSite = messageStatusBarSite;
        this.messageStore = messageStore;
        this.remoteManager = remoteManager;
    }

    @Override
    public final void initialize() {
        statusBarManager.addStatusBarSite(messageStatusBarSite);

        try {
            messageStore.initialize();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final void dispose() {
        try {
            messageStore.dispose();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        statusBarManager.removeStatusBarSite(messageStatusBarSite);
    }

    @Override
    public final void start() {
        try {
            List<NewsMessage> newsMessages = remoteManager.loadNewMessages(messageStore.getLastTimestamp());

            if (!newsMessages.isEmpty()) {
                messageStore.storeMessages(newsMessages);

                updateNotificationArea();

                messageStatusBarSite.showBalloonTip("There are new messages"); // todo localize
            }
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final void stop() {
        // No-op.
    }

    @Override
    public final MessageTimestamp getLastLoadedMessageTimestamp() {
        try {
            return messageStore.getLastTimestamp();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final List<NewsMessage> getMessages() {
        try {
            return messageStore.getMessages();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final List<NewsMessage> getUnreadMessages() {
        try {
            return messageStore.getUnreadMessages();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final NewsMessage markMessageAsRead(NewsMessage msg) {
        NewsMessage changedMsg = NewsMessage.createBuilder().copyFrom(msg).setUnread(false).build();

        try {
            messageStore.storeMessages(asList(changedMsg));
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        updateNotificationArea();

        return changedMsg;
    }

    @Override
    public final NewsMessage markMessageAsUnread(NewsMessage msg) {
        NewsMessage changedMsg = NewsMessage.createBuilder().copyFrom(msg).setUnread(true).build();

        try {
            messageStore.storeMessages(asList(changedMsg));
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        updateNotificationArea();

        return changedMsg;
    }

    @Override
    public final void deleteMessage(NewsMessage msg) {
        try {
            messageStore.deleteMessages(asList(msg));

            if (messageStore.getUnreadMessages().size() == 0) {
                messageStatusBarSite.setNotificationAreaActive(false);
                messageStatusBarSite.setNotificationTip(null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    private void updateNotificationArea() {
        int unreadMsgCount;

        try {
            unreadMsgCount = messageStore.getUnreadMessages().size();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        if (unreadMsgCount > 0) {
            messageStatusBarSite.setNotificationAreaActive(true);
            messageStatusBarSite
                    .setNotificationTip(MessageFormat.format(HAVE_UNREAD_MESSAGES_MSG_TMPL, unreadMsgCount));
        } else {
            messageStatusBarSite.setNotificationAreaActive(false);
            messageStatusBarSite.setNotificationTip(null);
        }
    }
}
