package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.httpserver.ui.messagesystem.*;
import com.google.inject.Inject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
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
public class MessageManagerImpl implements MessageManager {
    private static final Logger logger = LoggerFactory.getLogger(MessageManagerImpl.class);

    private static final String HAVE_UNREAD_MESSAGES_MSG_TMPL = "There are {0} unread messages";  // todo localize

    private static final int SHOW_NEWS_MESSAGES_BALLOON_DELAY_IN_MSEC = 60 * 1000;

    private final StatusBarManager statusBarManager;

    private final MessageStatusBarSite messageStatusBarSite;

    private final MessageStore messageStore;

    private final RemoteManager remoteManager;

    private boolean isSchedulerInitialized;

    private Scheduler scheduler;

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

        try {
            scheduler = new StdSchedulerFactory().getScheduler();

            scheduler.start();

            isSchedulerInitialized = true;
        } catch (SchedulerException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final void dispose() {
        if (isSchedulerInitialized) {
            try {
                scheduler.shutdown();

                isSchedulerInitialized = false;
            } catch (SchedulerException e) {
                throw new RuntimeException("internal error", e);
            }
        }

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

                scheduleShowBalloonTask();
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

    private void scheduleShowBalloonTask() {
        if (!isSchedulerInitialized) {
            logger.error("Cannot shedule show balloon task.");
            
            return;
        }

        ShowNewMessagesBalloonTask task = new ShowNewMessagesBalloonTask();

        JobDetail jobDetail = new JobDetail();

        jobDetail.setName("showNewMessagesBalloonTask");
        jobDetail.setJobClass(ShowNewMessagesBalloonJob.class);

        Map dataMap = jobDetail.getJobDataMap();

        dataMap.put("showNewMessagesBalloonTask", task);

        SimpleTrigger trigger = new SimpleTrigger();

        trigger.setName("showNewMessagesBalloonTrigger");
        trigger.setStartTime(new Date(System.currentTimeMillis() + SHOW_NEWS_MESSAGES_BALLOON_DELAY_IN_MSEC));
        trigger.setRepeatCount(0);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Cannot shedule show balloon task.", e);
        }
    }

    public class ShowNewMessagesBalloonTask {
        public final void showNewMessagesBalloon() {
            messageStatusBarSite.showBalloonTip("There are new messages"); // todo localize
        }
    }

    public static class ShowNewMessagesBalloonJob implements Job {
        @Override
        public final void execute(JobExecutionContext ctx) throws JobExecutionException {
            Map dataMap = ctx.getJobDetail().getJobDataMap(); 

            ShowNewMessagesBalloonTask task = (ShowNewMessagesBalloonTask) dataMap.get("showNewMessagesBalloonTask");

            task.showNewMessagesBalloon();
        }
    }
}
