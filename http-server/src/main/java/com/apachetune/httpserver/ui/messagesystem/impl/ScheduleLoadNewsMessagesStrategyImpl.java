package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.ScheduleLoadNewsMessagesStrategy;
import com.google.inject.Inject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * FIXDOC
 */
public class ScheduleLoadNewsMessagesStrategyImpl implements ScheduleLoadNewsMessagesStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MessageManagerImpl.class);

    private static final int LOAD_NEWS_MESSAGES_DELAY_AFTER_START_APP_IN_MSEC = 60 * 1000;

    private final Scheduler scheduler;

    @Inject
    public ScheduleLoadNewsMessagesStrategyImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public final void scheduleLoadNewsMessages(Runnable loadNewsMessagesTask) {
        JobDetail jobDetail = new JobDetail();

        jobDetail.setName("loadNewsMessagesTask");
        jobDetail.setJobClass(LoadNewsMessagesJob.class);

        Map dataMap = jobDetail.getJobDataMap();

        dataMap.put("loadNewsMessagesTask", loadNewsMessagesTask);

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

    public static class LoadNewsMessagesJob implements Job {
        @Override
        public final void execute(JobExecutionContext ctx) throws JobExecutionException {
            Map dataMap = ctx.getJobDetail().getJobDataMap();

            Runnable loadNewsMessagesTask = (Runnable) dataMap.get("loadNewsMessagesTask");

            loadNewsMessagesTask.run();
        }
    }
}
