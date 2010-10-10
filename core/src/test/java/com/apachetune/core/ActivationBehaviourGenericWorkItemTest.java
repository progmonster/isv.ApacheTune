package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
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
public class ActivationBehaviourGenericWorkItemTest {
    @Test
    public void testDefaultInactive() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");
        
        rootWorkItem.addChildWorkItem(workItem);
        
        assertThat(workItem.isActive()).isFalse();
    }

    @Test
    public void testActivate() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.activate();

        assertThat(workItem.isActive()).isTrue();
    }

    @Test
    public void testDeactivate() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.activate();
        workItem.deactivate();

        assertThat(workItem.isActive()).isFalse();
    }

    @Test
    public void testRaiseEventOnActivate() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

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
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

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
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

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
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

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
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        aa.activate();

        assertThat(a.isActive()).isTrue();
        assertThat(root.isActive()).isTrue();
        assertThat(b.isActive()).isFalse();
    }

    @Test
    public void testDeactivateItselfAndChildren() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        a.activate();
        root.deactivate();

        assertThat(root.isActive()).isFalse();
        assertThat(a.isActive()).isFalse();
        assertThat(aa.isActive()).isFalse();
    }
    
    @Test
    public void testDeactivateBranchOnActivationAnother() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        aa.activate();

        root.addActivationListener(new ActivationListener() {
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
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        b.activate();

        root.addActivationListener(new ActivationListener() {
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
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        a.activate();

        assertThat(root.getActiveChild()).isEqualTo(a);
        assertThat(a.getActiveChild()).isEqualTo(a);
    }

    @Test
    public void testGetDirectActiveChild() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        aa.activate();

        assertThat(root.getDirectActiveChild()).isEqualTo(a);
        assertThat(aa.getDirectActiveChild()).isNull();
    }

    @Test
    public void testNoActiveChildIfNotActivated() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        b.activate();

        assertThat(a.getActiveChild()).isNull();
        assertThat(aa.getActiveChild()).isNull();
    }


    @Test
    public void testNoDirectActiveChildIfNotActivated() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        b.activate();

        assertThat(a.getDirectActiveChild()).isNull();
        assertThat(aa.getDirectActiveChild()).isNull();
    }

    @Test
    public void testDeactivateOnRemovingFromParentWorkItem() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        a.activate();

        root.removeDirectChildWorkItem(a);

        assertThat(a.isActive()).isFalse();
    }

    @Test
    public void testDeactivateOnDispose() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        a.initialize();

        a.activate();

        a.dispose();

        assertThat(a.isActive()).isFalse();
    }
}