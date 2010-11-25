package com.apachetune.httpserver.ui.updating.impl;

import com.apachetune.httpserver.ui.updating.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import java.net.URL;

import static com.apachetune.httpserver.Constants.EMPTY_CHECK_UPDATE_DELAY_IN_MSEC;
import static com.apachetune.httpserver.Constants.NO_CHECK_UPDATE_NEEDS;
import static com.apachetune.httpserver.ui.updating.UpdateInfo.createNoUpdateInfo;

/**
 * FIXDOC
 */
@RunWith(JMock.class)
public class UpdateManagerImplTest {
    private final Mockery mockCtx = new JUnit4Mockery();

    private UpdateConfiguration mockUpdateConfiguration;

    private RemoteManager mockRemoteManager;

    private Scheduler mockScheduler;

    private UpdateInfoDialog mockUpdateInfoDialog;

    private OpenWebPageHelper mockOpenWebPageHelper;

    @Before
    public void prepare_test() {
        mockUpdateConfiguration = mockCtx.mock(UpdateConfiguration.class);

        mockRemoteManager = mockCtx.mock(RemoteManager.class);

        mockScheduler = mockCtx.mock(Scheduler.class);

        mockUpdateInfoDialog = mockCtx.mock(UpdateInfoDialog.class);

        mockOpenWebPageHelper = mockCtx.mock(OpenWebPageHelper.class);
    }

    @Test
    public void test_no_scheduling_check_for_update_on_initialize() {
        UpdateManager testSubj =
                new UpdateManagerImpl(NO_CHECK_UPDATE_NEEDS, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_skip_checking_for_update_when_it_disabled() {
        mockCtx.checking(new Expectations() {{
            atLeast(1).of(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(false));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_scheduling_a_check_for_update() throws Exception {
        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(60 * 1000, mockUpdateConfiguration, mockRemoteManager, mockScheduler,
                        mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_no_update_behaviour() throws Exception {
        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(createNoUpdateInfo()));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_update_and_user_accepted_update() throws Exception {
        final UpdateInfo fakeUpdateInfo = UpdateInfo.create("fake-app-full-name", new URL("http://apachetune.com/update"));

        final UpdateInfoDialog.UserActionOnUpdate fakeUserAction =
                new UpdateInfoDialog.UserActionOnUpdate(true, true);

        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(fakeUpdateInfo));

            one(mockUpdateInfoDialog).showHasUpdate(fakeUpdateInfo);
            will(returnValue(fakeUserAction));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(true);

            one(mockOpenWebPageHelper).openWebPage(fakeUpdateInfo.getUserFriendlyUpdatePageUrl());
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_update_but_user_rejected_update() throws Exception {
        final UpdateInfo fakeUpdateInfo =
                UpdateInfo.create("fake-app-full-name", new URL("http://apachetune.com/update"));

        final UpdateInfoDialog.UserActionOnUpdate fakeUserAction =
                new UpdateInfoDialog.UserActionOnUpdate(false, false);

        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(fakeUpdateInfo));

            one(mockUpdateInfoDialog).showHasUpdate(fakeUpdateInfo);
            will(returnValue(fakeUserAction));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(false);
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_handling_update_exception_during_checking_update_on_start() throws Exception {
        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new UpdateException("fake-exception")));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_handling_update_during_manual_update_check() throws Exception {
        final UpdateInfo fakeUpdateInfo =
                UpdateInfo.create("fake-app-full-name", new URL("http://apachetune.com/update"));

        final UpdateInfoDialog.UserActionOnUpdate fakeUserAction =
                new UpdateInfoDialog.UserActionOnUpdate(true, true);

        mockCtx.checking(new Expectations() {{
            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(fakeUpdateInfo));

            one(mockUpdateInfoDialog).showHasUpdate(fakeUpdateInfo);
            will(returnValue(fakeUserAction));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(true);

            one(mockOpenWebPageHelper).openWebPage(fakeUpdateInfo.getUserFriendlyUpdatePageUrl());
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(NO_CHECK_UPDATE_NEEDS, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();

        testSubj.checkForUpdate();

        testSubj.dispose();
    }

    @Test
    public void test_handling_no_update_during_manual_update_check() throws Exception {
        final UpdateInfo noUpdateInfo = createNoUpdateInfo();

        final UpdateInfoDialog.UserActionOnNoUpdate fakeUserAction =
                new UpdateInfoDialog.UserActionOnNoUpdate(true);

        mockCtx.checking(new Expectations() {{
            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(noUpdateInfo));

            one(mockUpdateInfoDialog).showHasNoUpdate();
            will(returnValue(fakeUserAction));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(true);
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(NO_CHECK_UPDATE_NEEDS, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();

        testSubj.checkForUpdate();

        testSubj.dispose();
    }

    @Test
    public void test_handling_error_during_manual_update_check() throws Exception {
        final UpdateInfoDialog.UserActionOnUpdateError fakeUserAction =
                new UpdateInfoDialog.UserActionOnUpdateError(true);

        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        final UpdateException fakeUpdateException = new UpdateException("fake-exception");

        mockCtx.checking(new Expectations() {{
            one(mockRemoteManager).checkUpdateAvailable();
            will(throwException(fakeUpdateException));

            one(mockUpdateInfoDialog).showUpdateCheckingError(fakeUpdateException);
            will(returnValue(fakeUserAction));

            // todo send error report
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(NO_CHECK_UPDATE_NEEDS, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockUpdateInfoDialog, mockOpenWebPageHelper, null);

        testSubj.initialize();

        testSubj.checkForUpdate();

        testSubj.dispose();
    }
}
