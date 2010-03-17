package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import com.apachetune.core.utils.BooleanValue;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.fail;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class GenericWorkItemLifecycleTest {
    @Test
    public void testOnInitializedEvent() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();
        
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        final BooleanValue isRaised = new BooleanValue();

        workItem.addLifecycleListener(new WorkItemLifecycleListener() {
            public void onInitialized(WorkItem workItem) {
                isRaised.value = true;
            }

            public void onDisposed(WorkItem workItem) {
                // No-op.
            }
        });

        workItem.initialize();

        assertTrue(isRaised.value);
    }

    @Test
    public void testOnDisposedEvent() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        final BooleanValue isRaised = new BooleanValue();

        workItem.addLifecycleListener(new WorkItemLifecycleListener() {
            public void onInitialized(WorkItem workItem) {
                // No-op.
            }

            public void onDisposed(WorkItem workItem) {
                isRaised.value = true;
            }
        });

        workItem.initialize();
        workItem.dispose();

        assertTrue(isRaised.value);
    }

    @Test
    public void testRemoveListener() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItemLifecycleListener listener = new WorkItemLifecycleListener() {
            public void onInitialized(WorkItem workItem) {
                fail();
            }

            public void onDisposed(WorkItem workItem) {
                fail();
            }
        };

        workItem.addLifecycleListener(listener);
        workItem.removeLifecycleListener(listener);

        workItem.initialize();
        workItem.dispose();
    }

    @Test
    public void testRemoveAllListeners() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItemLifecycleListener listener = new WorkItemLifecycleListener() {
            public void onInitialized(WorkItem workItem) {
                fail();
            }

            public void onDisposed(WorkItem workItem) {
                fail();
            }
        };

        workItem.addLifecycleListener(listener);
        workItem.removeAllLifecycleListeners();
        
        workItem.initialize();
        workItem.dispose();
    }

    @Test
    public void testFailOnInitializeAfterDispose() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.initialize();
        workItem.dispose();

        try {
            workItem.initialize();

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }
}
