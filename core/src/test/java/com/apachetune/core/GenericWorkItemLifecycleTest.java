package com.apachetune.core;

import com.apachetune.core.utils.BooleanValue;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;


/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class GenericWorkItemLifecycleTest extends WorkItemAbstractTest {
    @Test
    public void testOnInitializedEvent() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

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

        assertThat(isRaised.value).isTrue();
    }

    @Test
    public void testOnDisposedEvent() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

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

        assertThat(isRaised.value).isTrue();
    }

    @Test
    public void testRemoveListener() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

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
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

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

    @Test(expected = Exception.class)
    public void testFailOnInitializeAfterDispose() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.initialize();
        workItem.dispose();

        workItem.initialize();
    }
}
