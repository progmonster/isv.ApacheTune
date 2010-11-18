package com.apachetune.httpserver.ui.messagesystem.messagedialog;

import com.apachetune.core.ui.NPresenter;
import com.apachetune.httpserver.ui.messagesystem.MessageManager;
import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.MessageStoreDataChangedListener;
import com.apachetune.httpserver.ui.messagesystem.NewsMessage;
import com.google.inject.Inject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class MessagePresenter extends NPresenter<MessageView> implements MessageStoreDataChangedListener {
    private static final Logger logger = LoggerFactory.getLogger(MessagePresenter.class);

    private static final int MARK_MESSAGE_AS_READ_DELAY_IN_MSEC = 10000;

    private final MessageStore messageStore;

    private final MessageManager messageManager;

    private final Scheduler scheduler;

    private NewsMessage msgToMarkAsRead;

    private final Object markAsReadMsgLocker = new Object();

    @Inject
    public MessagePresenter(MessageStore messageStore, MessageManager messageManager) {
        this.messageStore = messageStore;
        this.messageManager = messageManager;

        try {
            scheduler = new StdSchedulerFactory().getScheduler();

            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException("internal error", e);
        }
    }

    @Override
    public final void onViewReady() {
        messageStore.addDataChangedListener(this);

        updateMessageControls();       
    }

    @Override
    public final void onCloseView() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException("internal error", e);
        }

        messageStore.removeDataChangedListener(this);
    }

    @Override
    public final void onStoredDataChanged() {
        getView().notifyDataChanged();

        checkDeleteMessageFromMarkAsReadSchedule();

        updateMessageControls();
    }

    public final void onSelectMessages() {
        if (getView().getSelectedMessages().size() == messageManager.getMessages().size()) {
            getView().unselectAllMessages();
        } else {
            getView().selectAllMessages();
        }
    }

    public final void onMarkMessagesAsUnread() {
        Collection<NewsMessage> messages = getSelectedMessages();

        for (NewsMessage msg : messages) {
            messageManager.markMessageAsUnread(msg);
        }

        getView().unselectAllMessages();
    }

    public final void onCurrentMessageChanged(NewsMessage msg) {
        safeRemoveMessageFromMarkAsReadSchedule();

        if (msg == null) {
            return;
        }

        if (msg.isUnread()) {
            scheduleMessageToMarkAsRead(msg);
        }
    }

    public final void onMessageDelete(NewsMessage msg) {
        messageManager.deleteMessage(msg);

        updateMessageControls();
    }

    public final void onMessagesDelete() {
        Collection<NewsMessage> messages = getSelectedMessages();

        if (messages.size() >= 2) {
            if (showConfirmDialog(
                    (Component) getView(),
                    "Do you want to delete selected messages?", // TODO localize
                    "Messages deletion", // todo localize
                    YES_NO_OPTION, QUESTION_MESSAGE) == NO_OPTION) {
                return;
            }
        }

        for (NewsMessage msg : messages) {
            messageManager.deleteMessage(msg);
        }

        updateMessageControls();
    }

    private List<NewsMessage> getSelectedMessages() {
        List<NewsMessage> messages = getView().getSelectedMessages();

        if (messages.size() == 0) {
            NewsMessage msg = getView().getCurrentMessage();

            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private void updateMessageControls() {
        getView().setMessageControlsEnabled(!messageManager.getMessages().isEmpty());
    }

    private void scheduleMessageToMarkAsRead(NewsMessage msg) {
        synchronized (markAsReadMsgLocker) {
            safeRemoveMessageFromMarkAsReadSchedule();

            MarkMessageAsReadTask task = new MarkMessageAsReadTask();

            JobDetail jobDetail = new JobDetail();

            jobDetail.setName("markMessageAsReadTask");
            jobDetail.setJobClass(MarkMessageAsReadJob.class);

            Map dataMap = jobDetail.getJobDataMap();

            dataMap.put("markMessageAsReadTask", task);

            SimpleTrigger trigger = new SimpleTrigger();

            trigger.setName("markMessageAsReadTrigger");
            trigger.setStartTime(new Date(System.currentTimeMillis() + MARK_MESSAGE_AS_READ_DELAY_IN_MSEC));
            trigger.setRepeatCount(0);

            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                logger.error("Cannot schedule mark message as read task.", e);
            }

            msgToMarkAsRead = msg;
        }
    }

    private void safeRemoveMessageFromMarkAsReadSchedule() {
        synchronized (markAsReadMsgLocker) {
            msgToMarkAsRead = null;

            try {
                scheduler.deleteJob("markMessageAsReadTask", "DEFAULT");
            } catch (SchedulerException e) {
                logger.error("Cannot unschedule mark message as read task.", e);
            }
        }
    }

    private void checkDeleteMessageFromMarkAsReadSchedule() {
        synchronized (markAsReadMsgLocker) {
            if (msgToMarkAsRead == null) {
                return;
            }

            for (NewsMessage msg : messageManager.getUnreadMessages()) {
                if (msg.equals(msgToMarkAsRead)) {
                    return;
                }
            }

            safeRemoveMessageFromMarkAsReadSchedule();
        }
    }

    private void onMarkMessageAsRead() {
        synchronized (markAsReadMsgLocker) {
            if (msgToMarkAsRead == null) {
                return;
            }

            NewsMessage markedAsReadMsg = messageManager.markMessageAsRead(msgToMarkAsRead);

            safeRemoveMessageFromMarkAsReadSchedule();

            getView().notifyDataChanged();

            getView().setCurrentMessage(markedAsReadMsg);
        }
    }

    private class MarkMessageAsReadTask {
        public final void execute() {
            onMarkMessageAsRead();
        }
    }

    public static class MarkMessageAsReadJob implements Job {
        @Override
        public final void execute(JobExecutionContext ctx) throws JobExecutionException {
            Map dataMap = ctx.getJobDetail().getJobDataMap();

            MarkMessageAsReadTask task = (MarkMessageAsReadTask) dataMap.get("markMessageAsReadTask");

            task.execute();
        }
    }
}
