package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.messagesystem.*;
import com.google.inject.Inject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * FIXDOC
 */
public class MessageManagerImpl implements MessageManager, MessageStoreDataChangedListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageManagerImpl.class);

    private static final String NO_UNREAD_MESSAGES_NOTIFICATION_TIP_MSG = "There are no unread messages.";
            // todo localize

    private static final String HAVE_UNREAD_MESSAGES_MSG_TMPL = "There are {0} unread messages";  // todo localize

    private static final int LOAD_NEWS_MESSAGES_DELAY_AFTER_START_APP_IN_MSEC = 60 * 1000;

    private final StatusBarManager statusBarManager;

    private final MessageStatusBarSite messageStatusBarSite;

    private final MessageStore messageStore;

    private final RemoteManager remoteManager;

    private Scheduler scheduler;

    @Inject
    public MessageManagerImpl(StatusBarManager statusBarManager,
                              MessageStatusBarSite messageStatusBarSite,
                              MessageStore messageStore, RemoteManager remoteManager,
                              Scheduler scheduler) {
        this.statusBarManager = statusBarManager;
        this.messageStatusBarSite = messageStatusBarSite;
        this.messageStore = messageStore;
        this.remoteManager = remoteManager;
        this.scheduler = scheduler;
    }

    @Override
    public final void initialize() {
        messageStatusBarSite.initialize();

        statusBarManager.addStatusBarSite(messageStatusBarSite);

        try {
            messageStore.initialize();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        messageStore.addDataChangedListener(this);
    }

    @Override
    public final void dispose() {
        messageStore.removeDataChangedListener(this);

        try {
            messageStore.dispose();
        } catch (SQLException e) {
            throw new RuntimeException("internal error", e);
        }

        statusBarManager.removeStatusBarSite(messageStatusBarSite);
        messageStatusBarSite.dispose();
    }

    @Override
    public final void start() {
        updateNotificationArea();

        scheduleLoadNewMessagesTask();
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

    private void scheduleLoadNewMessagesTask() {
        LoadNewsMessagesTask task = new LoadNewsMessagesTask();

        JobDetail jobDetail = new JobDetail();

        jobDetail.setName("loadNewsMessagesTask");
        jobDetail.setJobClass(LoadNewsMessagesJob.class);

        Map dataMap = jobDetail.getJobDataMap();

        dataMap.put("loadNewsMessagesTask", task);

        SimpleTrigger trigger = new SimpleTrigger();

        trigger.setName("loadNewsMessagesTrigger");
        trigger.setStartTime(new Date(System.currentTimeMillis() + LOAD_NEWS_MESSAGES_DELAY_AFTER_START_APP_IN_MSEC));
        trigger.setRepeatCount(0);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Cannot schedule load new messages task.", e);
        }
    }

    public class LoadNewsMessagesTask {
        public final void execute() {
            List<NewsMessage> newsMessages = remoteManager.loadNewMessages(getLastLoadedMessageTimestamp());

            if (newsMessages.size() > 0) {
                messageStore.storeMessages(newsMessages);

                updateNotificationArea();
                messageStatusBarSite.showBalloonTip("There are new messages"); // todo localize
            }
        }
    }

    public static class LoadNewsMessagesJob implements Job {
        @Override
        public final void execute(JobExecutionContext ctx) throws JobExecutionException {
            Map dataMap = ctx.getJobDetail().getJobDataMap(); 

            LoadNewsMessagesTask task = (LoadNewsMessagesTask) dataMap.get("loadNewsMessagesTask");

            task.execute();
        }
    }
}
