package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.messagesystem.*;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static java.util.Arrays.asList;

/**
 * FIXDOC
 */
public class MessageManagerImpl implements MessageManager, MessageStoreDataChangedListener {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger logger = LoggerFactory.getLogger(MessageManagerImpl.class);

    private static final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(MessageManagerImpl.class);

    private static final String NO_UNREAD_MESSAGES_NOTIFICATION_TIP_MSG =
            resourceBundle.getString("messageManagerImpl.noUnreadMessagesNotificationTipMessageText");

    private static final String HAVE_UNREAD_MESSAGES_MSG_TMPL =
            resourceBundle.getString("messageManagerImpl.haveUnreadMessagesText");

    private final StatusBarManager statusBarManager;

    private final MessageStatusBarSite messageStatusBarSite;

    private final MessageStore messageStore;

    private final RemoteManager remoteManager;

    private final ScheduleLoadNewsMessagesStrategy scheduleLoadNewsMessagesStrategy;

    @Inject
    public MessageManagerImpl(StatusBarManager statusBarManager,
                              MessageStatusBarSite messageStatusBarSite,
                              MessageStore messageStore, RemoteManager remoteManager,
                              ScheduleLoadNewsMessagesStrategy scheduleLoadNewsMessagesStrategy) {
        this.statusBarManager = statusBarManager;
        this.messageStatusBarSite = messageStatusBarSite;
        this.messageStore = messageStore;
        this.remoteManager = remoteManager;
        this.scheduleLoadNewsMessagesStrategy = scheduleLoadNewsMessagesStrategy;
    }

    @Override
    public final void initialize() {
        messageStatusBarSite.initialize();

        statusBarManager.addStatusBarSite(messageStatusBarSite);

        try {
            messageStore.initialize();
        } catch (SQLException e) {
            throw createRuntimeException(e);
        }

        messageStore.addDataChangedListener(this);
    }

    @Override
    public final void dispose() {
        messageStore.removeDataChangedListener(this);

        try {
            messageStore.dispose();
        } catch (SQLException e) {
            throw createRuntimeException(e);
        }

        statusBarManager.removeStatusBarSite(messageStatusBarSite);
        messageStatusBarSite.dispose();
    }

    @Override
    public final void start() {
        updateNotificationArea();

        scheduleLoadNewsMessagesStrategy.scheduleLoadNewsMessages(new Runnable() {
            @Override
            public final void run() {
                List<NewsMessage> newsMessages = remoteManager.loadNewMessages(getLastLoadedMessageTimestamp());

                if (newsMessages.size() > 0) {
                    messageStore.storeMessages(newsMessages);

                    updateNotificationArea();
                    messageStatusBarSite.showBalloonTip(resourceBundle.getString("messageManagerImpl.haveNewsMessagesBalloonTipText"));
                }
            }
        });
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

        updateNotificationArea();

        return changedMsg;
    }

    @Override
    public final NewsMessage markMessageAsUnread(NewsMessage msg) {
        NewsMessage changedMsg = NewsMessage.createBuilder().copyFrom(msg).setUnread(true).build();

        messageStore.storeMessages(asList(changedMsg));

        updateNotificationArea();

        return changedMsg;
    }

    @Override
    public final void deleteMessage(NewsMessage msg) {
        messageStore.deleteMessages(asList(msg));

        if (messageStore.getUnreadMessages().size() == 0) {
            messageStatusBarSite.setNotificationAreaActive(false);
            messageStatusBarSite.setNotificationTip(NO_UNREAD_MESSAGES_NOTIFICATION_TIP_MSG);
        }
    }

    @Override
    public final void onStoredDataChanged() {
        updateNotificationArea();        
    }

    private void updateNotificationArea() {
        int unreadMsgCount;

        unreadMsgCount = messageStore.getUnreadMessages().size();

        if (unreadMsgCount > 0) {
            messageStatusBarSite.setNotificationAreaActive(true);
            messageStatusBarSite
                    .setNotificationTip(MessageFormat.format(HAVE_UNREAD_MESSAGES_MSG_TMPL, unreadMsgCount));
        } else {
            messageStatusBarSite.setNotificationAreaActive(false);
            messageStatusBarSite.setNotificationTip(NO_UNREAD_MESSAGES_NOTIFICATION_TIP_MSG);
        }
    }
}
