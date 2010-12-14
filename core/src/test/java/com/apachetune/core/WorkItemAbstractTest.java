package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.quartz.Scheduler;

/**
 * FIXDOC
 */
public class WorkItemAbstractTest {
    private Mockery mockCtx;

    private RootWorkItem rootWorkItem;

    @Before
    public void prepare_root_work_item() {
        mockCtx = new Mockery();

        Scheduler mockScheduler = mockCtx.mock(Scheduler.class);

        rootWorkItem = new RootWorkItemImpl(mockScheduler, null);
    }

    @After
    public void assert_is_satisfied() {
        mockCtx.assertIsSatisfied();
    }

    protected final Mockery getMockContext() {
        return mockCtx;
    }

    protected final RootWorkItem getRootWorkItem() {
        return rootWorkItem;
    }
}
