package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.errorreportsystem.SendErrorReportEvent;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.updating.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Date;
import java.util.Map;

import static com.apachetune.core.Constants.ON_SEND_ERROR_REPORT_EVENT;
import static com.apachetune.httpserver.Constants.*;

/**
 * FIXDOC
 */
public class UpdateManagerImpl implements UpdateManager {
    private static final Logger logger = LoggerFactory.getLogger(UpdateManagerImpl.class);

    private final long updateDelayInMSec;

    private final UpdateConfiguration updateConfiguration;

    private final RemoteManager remoteManager;

    private final Scheduler scheduler;

    private final UpdateInfoDialog updateInfoDialog;

    private final OpenWebPageHelper openWebPageHelper;

    private final HttpServerWorkItem httpServerWorkItem;

    private final JFrame mainFrame;

    private final Object checkForUpdateLock = new Object();

    @Inject
    public UpdateManagerImpl(@Named(CHECK_UPDATE_DELAY_IN_MSEC_PROP) long updateDelayInMSec,
                             UpdateConfiguration updateConfiguration, RemoteManager remoteManager,
                             Scheduler scheduler, UpdateInfoDialog updateInfoDialog,
                             OpenWebPageHelper openWebPageHelper,
                             HttpServerWorkItem httpServerWorkItem, JFrame mainFrame) {
        this.updateDelayInMSec = updateDelayInMSec;
        this.updateConfiguration = updateConfiguration;
        this.remoteManager = remoteManager;
        this.scheduler = scheduler;
        this.updateInfoDialog = updateInfoDialog;
        this.openWebPageHelper = openWebPageHelper;
        this.httpServerWorkItem = httpServerWorkItem;
        this.mainFrame = mainFrame;
    }

    @Override
    public final void initialize() {
        if ((updateDelayInMSec == NO_CHECK_UPDATE_NEEDS) || !updateConfiguration.getCheckUpdateFlag()) {
            return;
        }

        if (updateDelayInMSec == EMPTY_CHECK_UPDATE_DELAY_IN_MSEC) {
            try {
                doCheckForUpdate(false);
            } catch (UpdateException e) {
                logger.error("Error during checking for update.", e);
            }
        } else {
            scheduleCheckForUpdate();
        }
    }

    @Override
    public final void dispose() {
        // No-op.
    }

    @Override
    public final void checkForUpdate() {
        try {
            boolean hasUpdate = doCheckForUpdate(true);

            if (!hasUpdate) {
                UpdateInfoDialog.UserActionOnNoUpdate userAction = updateInfoDialog.showHasNoUpdate();

                updateConfiguration.storeCheckUpdateFlag(userAction.isUserEnableCheckForUpdateOnStart());
            }
        } catch (UpdateException e) {
            logger.error("Error during checking for update.", e);
            
            UpdateInfoDialog.UserActionOnUpdateError userAction = updateInfoDialog.showUpdateCheckingError(e);

            if (userAction.isUserAgreeSendErrorReport()) {
                httpServerWorkItem.raiseEvent(ON_SEND_ERROR_REPORT_EVENT, new SendErrorReportEvent(mainFrame,
                        "Check for update error", e));
            }
        }
    }

    private boolean doCheckForUpdate(boolean forceCheckForUpdate) throws UpdateException {
        synchronized (checkForUpdateLock) {
            if (!forceCheckForUpdate && !updateConfiguration.getCheckUpdateFlag()) {
                return false;
            }

            UpdateInfo updateInfo = remoteManager.checkUpdateAvailable();

            if (!updateInfo.hasUpdate()) {
                return false;
            }

            UpdateInfoDialog.UserActionOnUpdate userAction = updateInfoDialog.showHasUpdate(updateInfo);

            if (userAction.isUserAgreeUpdate()) {
                openWebPageHelper.openWebPage(updateInfo.getUserFriendlyUpdatePageUrl());
            }

            updateConfiguration.storeCheckUpdateFlag(userAction.isUserEnableCheckForUpdateOnStart());

            return true;
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
            try {
                doCheckForUpdate(false);
            } catch (UpdateException e) {
                logger.error("Error during checking for update.", e);
            }
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
