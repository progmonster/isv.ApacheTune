package com.apachetune.core;

import com.apachetune.core.impl.*;
import com.apachetune.core.utils.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class ActivationBehaviourGenericWorkItemTest {
    @Test
    public void testDefaultInactive() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");
        
        rootWorkItem.addChildWorkItem(workItem);
        
        assertFalse(workItem.isActive());
    }

    @Test
    public void testActivate() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.activate();

        assertTrue(workItem.isActive());
    }

    @Test
    public void testDeactivate() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("SIMPLE_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.activate();
        workItem.deactivate();

        assertFalse(workItem.isActive());
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

        assertTrue(wasRaised.value);
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

        assertTrue(wasRaised.value);
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

        assertTrue(a.isActive());
        assertTrue(root.isActive());
        assertFalse(b.isActive());
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

        assertFalse(root.isActive());
        assertFalse(a.isActive());
        assertFalse(aa.isActive());
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

        assertFalse(aa.isActive());
        assertFalse(a.isActive());
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

        assertEquals(root.getActiveChild(), a);
        assertEquals(a.getActiveChild(), a);
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

        assertNull(a.getActiveChild());
        assertNull(aa.getActiveChild());
    }

    @Test
    public void testDeactivateOnRemovingFromParentWorkItem() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        a.activate();

        root.removeChildWorkItem(a);

        assertFalse(a.isActive());
    }

    @Test
    public void testDeactivateOnDispose() {
        RootWorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        a.initialize();

        a.activate();

        a.dispose();

        assertFalse(a.isActive());
    }
}