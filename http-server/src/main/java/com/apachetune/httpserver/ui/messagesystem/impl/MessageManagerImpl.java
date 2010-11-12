package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.messagesystem.*;
import com.apachetune.httpserver.ui.messagesystem.messagedialog.MessageSmartPart;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * FIXDOC
 */
public class MessageManagerImpl implements MessageManager {
    private final Provider<MessageSmartPart> messageSmartPartProvider;

    private final StatusBarManager statusBarManager;

    private final HttpServerWorkItem httpServerWorkItem;

    private final MessageStatusBarSite messageStatusBarSite;

    private final MessageStore messageStore;

    private final RemoteManager remoteManager;

    @Inject
    public MessageManagerImpl(StatusBarManager statusBarManager, Provider<MessageSmartPart> messageSmartPartProvider,
                              HttpServerWorkItem httpServerWorkItem, MessageStatusBarSite messageStatusBarSite,
                              MessageStore messageStore, RemoteManager remoteManager) {
        this.statusBarManager = statusBarManager;
        this.messageSmartPartProvider = messageSmartPartProvider;
        this.httpServerWorkItem = httpServerWorkItem;
        this.messageStatusBarSite = messageStatusBarSite;
        this.messageStore = messageStore;
        this.remoteManager = remoteManager;
    }

    @Override
    public final void initialize() {
        statusBarManager.addStatusBarSite(messageStatusBarSite);
    }

    @Override
    public final void dispose() {
        statusBarManager.removeStatusBarSite(messageStatusBarSite);
    }

    @Override
    public final void start() {
        List<NewsMessage> newsMessages = remoteManager.loadNewMessages(messageStore.getLastTimestamp());

        if (!newsMessages.isEmpty()) {
            messageStore.storeMessages(newsMessages);

            messageStatusBarSite.setNotificationAreaActive(true);
            messageStatusBarSite.setNotificationTip("There are new messages"); // todo localize
            messageStatusBarSite.showBalloonTip("There are new messages"); // todo localize
        }
    }

    @Override
    public final void stop() {
        // No-op.
    }

    @Override
    public final MessageTimestamp getLastLoadedMessageTimestamp() {
        return messageStore.getLastTimestamp();
    }

    @Override
    public final List<NewsMessage> getMessages() {
        return messageStore.getMessages();
    }

    @Override
    public final List<NewsMessage> getUnreadMessages() {
        return messageStore.getUnreadMessages();
    }

    @Override
    public final NewsMessage markMessageAsRead(NewsMessage msg) {
        NewsMessage changedMsg = NewsMessage.createBuilder().copyFrom(msg).setUnread(false).build();

        messageStore.storeMessages(asList(changedMsg));

        if (messageStore.getUnreadMessages().size() == 0) {
            messageStatusBarSite.setNotificationAreaActive(false);
            messageStatusBarSite.setNotificationTip(null);
        }

        return changedMsg;
    }

    @Override
    public final NewsMessage markMessageAsUnread(NewsMessage msg) {
        NewsMessage changedMsg = NewsMessage.createBuilder().copyFrom(msg).setUnread(true).build();

        messageStore.storeMessages(asList(changedMsg));

        messageStatusBarSite.setNotificationAreaActive(true);
        messageStatusBarSite.setNotificationTip("There are new messages"); // todo localize

        return changedMsg;
    }

    @Override
    public final void deleteMessage(NewsMessage msg) {
        messageStore.deleteMessages(asList(msg));

        if (messageStore.getUnreadMessages().size() == 0) {
            messageStatusBarSite.setNotificationAreaActive(false);
            messageStatusBarSite.setNotificationTip(null);
        }
    }
}
