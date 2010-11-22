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
import static com.apachetune.httpserver.ui.updating.HasUpdateMessageDialog.UpdateAction.NEED_UPDATE;
import static com.apachetune.httpserver.ui.updating.HasUpdateMessageDialog.UpdateAction.SKIP_UPDATE;
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

    private HasUpdateMessageDialog mockHasUpdateMessageDialog;

    private OpenWebPageHelper mockOpenWebPageHelper;

    @Before
    public void prepare_test() {
        mockUpdateConfiguration = mockCtx.mock(UpdateConfiguration.class);

        mockRemoteManager = mockCtx.mock(RemoteManager.class);

        mockScheduler = mockCtx.mock(Scheduler.class);

        mockHasUpdateMessageDialog = mockCtx.mock(HasUpdateMessageDialog.class);

        mockOpenWebPageHelper = mockCtx.mock(OpenWebPageHelper.class);
    }

    @Test
    public void test_skip_checking_for_update_when_it_disabled() {
        mockCtx.checking(new Expectations() {{
            atLeast(1).of(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(false));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockHasUpdateMessageDialog, mockOpenWebPageHelper);

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
                        mockHasUpdateMessageDialog, mockOpenWebPageHelper);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_no_update_behaviour() {
        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(createNoUpdateInfo()));
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockHasUpdateMessageDialog, mockOpenWebPageHelper);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_update_and_user_accepted_update() throws Exception {
        final UpdateInfo fakeUpdateInfo = UpdateInfo.create(new URL("http://apachetune.com/update"));

        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(fakeUpdateInfo));

            one(mockHasUpdateMessageDialog).show(fakeUpdateInfo);
            will(returnValue(NEED_UPDATE));

            one(mockOpenWebPageHelper).openWebPage(fakeUpdateInfo.getUserFriendlyUpdatePageUrl());

            allowing(mockHasUpdateMessageDialog).isUserEnableCheckForUpdate();
            will(returnValue(true));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(true);
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockHasUpdateMessageDialog, mockOpenWebPageHelper);

        testSubj.initialize();
        testSubj.dispose();
    }

    @Test
    public void test_has_update_but_user_rejected_update() throws Exception {
        final UpdateInfo fakeUpdateInfo = UpdateInfo.create(new URL("http://apachetune.com/update"));

        mockCtx.checking(new Expectations() {{
            allowing(mockUpdateConfiguration).getCheckUpdateFlag();
            will(returnValue(true));

            one(mockRemoteManager).checkUpdateAvailable();
            will(returnValue(fakeUpdateInfo));

            one(mockHasUpdateMessageDialog).show(fakeUpdateInfo);
            will(returnValue(SKIP_UPDATE));

            allowing(mockHasUpdateMessageDialog).isUserEnableCheckForUpdate();
            will(returnValue(false));

            one(mockUpdateConfiguration).storeCheckUpdateFlag(false);
        }});

        UpdateManager testSubj =
                new UpdateManagerImpl(EMPTY_CHECK_UPDATE_DELAY_IN_MSEC, mockUpdateConfiguration, mockRemoteManager,
                        mockScheduler, mockHasUpdateMessageDialog, mockOpenWebPageHelper);

        testSubj.initialize();
        testSubj.dispose();
    }
}
