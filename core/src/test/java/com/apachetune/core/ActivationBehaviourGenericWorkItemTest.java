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
public class ActivationBehaviourGenericWorkItemTest extends WorkItemAbstractTest {
    @Test
    public void testDefaultInactive() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");
        
        getRootWorkItem().addChildWorkItem(workItem);
        
        assertThat(workItem.isActive()).isFalse();
    }

    @Test
    public void testActivate() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.activate();

        assertThat(workItem.isActive()).isTrue();
    }

    @Test
    public void testDeactivate() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.activate();
        workItem.deactivate();

        assertThat(workItem.isActive()).isFalse();
    }

    @Test
    public void testRaiseEventOnActivate() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        final BooleanValue wasRaised = new BooleanValue();

        workItem.addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                wasRaised.value = true;
            }

            public void onDeactivate(WorkItem workItem) {
                // No-op.
            }
        });

        workItem.activate();

        assertThat(wasRaised.value).isTrue();
    }

    @Test
    public void testNoEventOnActivateIfAlreadyActivated() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.activate();

        workItem.addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                fail();
            }

            public void onDeactivate(WorkItem workItem) {
                // No-op.
            }
        });

        workItem.activate();
    }

    @Test
    public void testRaiseEventOnDeactivate() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.activate();

        final BooleanValue wasRaised = new BooleanValue();

        workItem.addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                // No-op.
            }

            public void onDeactivate(WorkItem workItem) {
                wasRaised.value = true;
            }
        });

        workItem.deactivate();

        assertThat(wasRaised.value).isTrue();
    }

    @Test
    public void testNoEventOnDeactivateIfAlreadyDeActivated() {
        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(workItem);

        workItem.addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                // No-op.
            }

            public void onDeactivate(WorkItem workItem) {
                fail();
            }
        });

        workItem.deactivate();
    }

    @Test
    public void testActivateParents() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        aa.activate();

        assertThat(a.isActive()).isTrue();
        assertThat(getRootWorkItem().isActive()).isTrue();
        assertThat(b.isActive()).isFalse();
    }

    @Test
    public void testDeactivateItselfAndChildren() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        a.activate();
        getRootWorkItem().deactivate();

        assertThat(getRootWorkItem().isActive()).isFalse();
        assertThat(a.isActive()).isFalse();
        assertThat(aa.isActive()).isFalse();
    }
    
    @Test
    public void testDeactivateBranchOnActivationAnother() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        aa.activate();

        getRootWorkItem().addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                // No-op.
            }

            public void onDeactivate(WorkItem workItem) {
                fail();
            }
        });

        b.activate();

        assertThat(aa.isActive()).isFalse();
        assertThat(a.isActive()).isFalse();
    }

    @Test
    public void testActivationNonActiveChildOnlyInBranchToActivate() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        b.activate();

        getRootWorkItem().addActivationListener(new ActivationListener() {
            public void onActivate(WorkItem workItem) {
                fail();
            }

            public void onDeactivate(WorkItem workItem) {
                // No-op.
            }
        });

        aa.activate();
    }

    @Test
    public void testGetActiveChild() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        a.activate();

        assertThat(getRootWorkItem().getActiveChild()).isEqualTo(a);
        assertThat(a.getActiveChild()).isEqualTo(a);
    }

    @Test
    public void testGetDirectActiveChild() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        aa.activate();

        assertThat(getRootWorkItem().getDirectActiveChild()).isEqualTo(a);
        assertThat(aa.getDirectActiveChild()).isNull();
    }

    @Test
    public void testNoActiveChildIfNotActivated() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        b.activate();

        assertThat(a.getActiveChild()).isNull();
        assertThat(aa.getActiveChild()).isNull();
    }


    @Test
    public void testNoDirectActiveChildIfNotActivated() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        getRootWorkItem().addChildWorkItem(b);

        b.activate();

        assertThat(a.getDirectActiveChild()).isNull();
        assertThat(aa.getDirectActiveChild()).isNull();
    }

    @Test
    public void testDeactivateOnRemovingFromParentWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        a.activate();

        getRootWorkItem().removeDirectChildWorkItem(a);

        assertThat(a.isActive()).isFalse();
    }

    @Test
    public void testDeactivateOnDispose() {
        WorkItem a = new SimpleWorkItem("A");

        getRootWorkItem().addChildWorkItem(a);

        a.initialize();

        a.activate();

        a.dispose();

        assertThat(a.isActive()).isFalse();
    }
}