package com.apachetune.core;

import static com.apachetune.TestUtils.*;
import com.apachetune.core.impl.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;

import java.util.*;

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

        WorkItem addedWorkItem = workItem.getChildWorkItem("CHILD_TEST_WORK_ITEM");

        org.testng.Assert.assertEquals(addedWorkItem, childWorkItem);
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
    public void testRemoveChildWorkItemById() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        workItem.removeChildWorkItem(childWorkItem);
    }

    @Test
    public void testRemoveChildWorkItemByRef() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        workItem.removeChildWorkItem(childWorkItem.getId());
    }

    @Test
    public void testFailOnRemoveNonAddedChildWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        try {
            workItem.removeChildWorkItem(childWorkItem.getId());

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

        org.testng.Assert.assertEquals(childWorkItem.getParent(), workItem);
        org.testng.Assert.assertEquals(childWorkItem.getRootWorkItem(), rootWorkItem);
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

        org.testng.Assert.assertEquals(eventHistory, expectedEventHistory);
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