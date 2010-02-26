package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.apachetune.TestUtils.failBecauseExceptionWasExpected;
import static com.apachetune.TestUtils.reportExpectedException;
import static org.testng.Assert.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class ContainerBehaviourGenericWorkItemTest {
    @Test
    public void testAddChildWorkItem() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        workItem.setRootWorkItem(new RootWorkItemImpl());

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        WorkItem addedWorkItem = workItem.getDirectChildWorkItem("CHILD_TEST_WORK_ITEM");

        assertEquals(addedWorkItem, childWorkItem);
    }

    @Test
    public void testGetChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem aaa = new SimpleWorkItem("AAA");

        aa.addChildWorkItem(aaa);

        assertEquals(a.getChildWorkItem("AAA"), aaa);
    }

    @Test
    public void testHasDirectChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem aaa = new SimpleWorkItem("AAA");

        aa.addChildWorkItem(aaa);

        assertTrue(a.hasDirectChildWorkItem("AA"));
        assertFalse(a.hasDirectChildWorkItem("AAA"));
    }

    @Test
    public void testHasChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem ab = new SimpleWorkItem("AB");

        a.addChildWorkItem(ab);

        WorkItem aaa = new SimpleWorkItem("AAA");

        aa.addChildWorkItem(aaa);

        assertTrue(a.hasChildWorkItem("AAA"));
        assertFalse(aa.hasChildWorkItem("AB"));
    }

    @Test
    public void testNullOnNonExistingChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        assertNull(a.getChildWorkItem("NON_EXISTING_WORK_ITEM"));
    }

    @Test
    public void testFailOnDuplicateAddingOfChildWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);
        
        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        try {
            workItem.addChildWorkItem(childWorkItem);

            failBecauseExceptionWasExpected();
        } catch (IllegalArgumentException e) {
            reportExpectedException(e);
        }
    }

    @Test
    public void testRemoveDirectChildWorkItemById() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        workItem.removeDirectChildWorkItem(childWorkItem);
    }

    @Test
    public void testRemoveDirectChildWorkItemByRef() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        workItem.removeDirectChildWorkItem(childWorkItem.getId());
    }

    @Test
    public void testFailOnRemoveNonAddedDirectChildWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        try {
            workItem.removeDirectChildWorkItem(childWorkItem.getId());

            failBecauseExceptionWasExpected();
        } catch (IllegalArgumentException e) {
            reportExpectedException(e);
        }
    }

    @Test
    public void testSettingUpContextForChildrenWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        assertEquals(childWorkItem.getParent(), workItem);
        assertEquals(childWorkItem.getRootWorkItem(), rootWorkItem);
    }

    @Test
    public void testFailOnAddingToContainerWhichHasNoParent() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        try {
            workItem.addChildWorkItem(childWorkItem);

            failBecauseExceptionWasExpected();
        } catch (IllegalArgumentException e) {
            reportExpectedException(e);
        }
    }

    @Test
    public void testFailOnAddingToContainerEarlyAddedChildWorkItem() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(childWorkItem);

        try {
            rootWorkItem.addChildWorkItem(childWorkItem);

            failBecauseExceptionWasExpected();
        } catch (IllegalArgumentException e) {
            reportExpectedException(e);
        }
    }

    @Test
    public void testFailOnAddingWorkItemToHimself() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        try {
            workItem.addChildWorkItem(workItem);

            failBecauseExceptionWasExpected();
        } catch (IllegalArgumentException e) {
            reportExpectedException(e);
        }
    }

    @Test
    public void testHasAncestor() {
        WorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        assertTrue(a.hasAncestor(root));
        assertFalse(a.hasAncestor(b));
        assertFalse(a.hasAncestor(a));
    }

    @Test
    public void testEventRaising() {
        EventHistory eventHistory = new EventHistory();

        final RootWorkItem rootWorkItem = new RootWorkItemImpl();

        final WorkItem childWorkItemA = new TestEventWorkItem("CHILD_WORK_ITEM_A", eventHistory);

        final WorkItem childWorkItemAA = new TestEventWorkItem("CHILD_WORK_ITEM_AA", eventHistory);

        final WorkItem childWorkItemAB = new TestEventWorkItem("CHILD_WORK_ITEM_AB", eventHistory);

        final WorkItem childWorkItemB = new TestEventWorkItem("CHILD_WORK_ITEM_B", eventHistory);

        final WorkItem childWorkItemBA = new TestEventWorkItem("CHILD_WORK_ITEM_BA", eventHistory);

        final WorkItem childWorkItemBB = new TestEventWorkItem("CHILD_WORK_ITEM_BB", eventHistory);

        rootWorkItem.addChildWorkItem(childWorkItemA);
        rootWorkItem.addChildWorkItem(childWorkItemB);

        childWorkItemA.addChildWorkItem(childWorkItemAA);
        childWorkItemA.addChildWorkItem(childWorkItemAB);

        childWorkItemB.addChildWorkItem(childWorkItemBA);
        childWorkItemB.addChildWorkItem(childWorkItemBB);

        childWorkItemA.raiseEvent("TEST_EVENT", "any message");

        @SuppressWarnings({"serial"})
        EventHistory expectedEventHistory = new EventHistory() {{
            addEvent(childWorkItemA.getId(), "any message", "1");
            addEvent(childWorkItemA.getId(), "any message", "2");

            addEvent(childWorkItemAA.getId(), "any message", "1");
            addEvent(childWorkItemAA.getId(), "any message", "2");

            addEvent(childWorkItemAB.getId(), "any message", "1");
            addEvent(childWorkItemAB.getId(), "any message", "2");

            addEvent(childWorkItemB.getId(), "any message", "1");
            addEvent(childWorkItemB.getId(), "any message", "2");

            addEvent(childWorkItemBA.getId(), "any message", "1");
            addEvent(childWorkItemBA.getId(), "any message", "2");

            addEvent(childWorkItemBB.getId(), "any message", "1");
            addEvent(childWorkItemBB.getId(), "any message", "2");
        }};

        assertEquals(eventHistory, expectedEventHistory);
    }

    @Test
    public void testInitialize() {
        // TODO test initializing a work item tree.
    }
}

@SuppressWarnings({"serial"})
class EventHistory extends ArrayList<String> {
    public void addEvent(String eventId, String message, String handlerName) {
        assert eventId != null;
        assert message != null;
        assert handlerName != null;

        add(eventId + " :: " + message + " :: " + handlerName);
    }
}