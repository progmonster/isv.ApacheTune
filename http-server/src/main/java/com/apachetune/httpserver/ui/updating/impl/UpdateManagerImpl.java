package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.httpserver.ui.updating.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

import static com.apachetune.httpserver.Constants.CHECK_UPDATE_DELAY_IN_MSEC_PROP;
import static com.apachetune.httpserver.Constants.EMPTY_CHECK_UPDATE_DELAY_IN_MSEC;
import static com.apachetune.httpserver.ui.updating.HasUpdateMessageDialog.UpdateAction.NEED_UPDATE;

/**
 * FIXDOC
 */
public class UpdateManagerImpl implements UpdateManager {
    private static final Logger logger = LoggerFactory.getLogger(UpdateManagerImpl.class);

    private final long updateDelayInMSec;

    private final UpdateConfiguration updateConfiguration;

    private final RemoteManager remoteManager;

    private final Scheduler scheduler;

    private final HasUpdateMessageDialog hasUpdateMessageDialog;

    private final OpenWebPageHelper openWebPageHelper;

    private final Object checkForUpdateLock = new Object();

    @Inject
    public UpdateManagerImpl(@Named(CHECK_UPDATE_DELAY_IN_MSEC_PROP) long updateDelayInMSec,
                             UpdateConfiguration updateConfiguration, RemoteManager remoteManager,
                             Scheduler scheduler, HasUpdateMessageDialog hasUpdateMessageDialog,
                             OpenWebPageHelper openWebPageHelper) {
        this.updateDelayInMSec = updateDelayInMSec;
        this.updateConfiguration = updateConfiguration;
        this.remoteManager = remoteManager;
        this.scheduler = scheduler;
        this.hasUpdateMessageDialog = hasUpdateMessageDialog;
        this.openWebPageHelper = openWebPageHelper;
    }

    @Override
    public final void initialize() {
        if (!updateConfiguration.getCheckUpdateFlag()) {
            return;
        }

        if (updateDelayInMSec == EMPTY_CHECK_UPDATE_DELAY_IN_MSEC) {
            checkForUpdate();
        } else {
            scheduleCheckForUpdate();
        }
    }

    @Override
    public final void dispose() {
        // No-op.
    }

    private void checkForUpdate() {
        synchronized (checkForUpdateLock) {
            if (!updateConfiguration.getCheckUpdateFlag()) {
                return;
            }

            UpdateInfo updateInfo = remoteManager.checkUpdateAvailable();

            if (!updateInfo.hasUpdate()) {
                return;
            }

            if (hasUpdateMessageDialog.show(updateInfo) == NEED_UPDATE) {
                openWebPageHelper.openWebPage(updateInfo.getUserFriendlyUpdatePageUrl());
            }

            updateConfiguration.storeCheckUpdateFlag(hasUpdateMessageDialog.isUserEnableCheckForUpdate());
        }
    }

    private void scheduleCheckForUpdate() {
        CheckForUpdateTask task = new CheckForUpdateTask();

        JobDetail jobDetail = new JobDetail();

        jobDetail.setName("checkForUpdateTask");
        jobDetail.setJobClass(CheckForUpdateJob.class);

        Map dataMap = jobDetail.getJobDataMap();

        dataMap.put("checkForUpdateTask", task);

        SimpleTrigger trigger = new SimpleTrigger();

        trigger.setName("checkForUpdateTrigger");
        trigger.setStartTime(new Date(System.currentTimeMillis() + updateDelayInMSec));
        trigger.setRepeatCount(0);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Cannot schedule a check for update task.", e);
        }
    }

    public class CheckForUpdateTask {
        public final void execute() {
            checkForUpdate();
        }
    }

    public static class CheckForUpdateJob implements Job {
        @Override
        public final void execute(JobExecutionContext ctx) throws JobExecutionException {
            Map dataMap = ctx.getJobDetail().getJobDataMap();

            CheckForUpdateTask task = (CheckForUpdateTask) dataMap.get("checkForUpdateTask");

            task.execute();
        }
    }
}
