package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ContainerBehaviourGenericWorkItemTest {
    @Test
    public void testAddChildWorkItem() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        workItem.setRootWorkItem(new RootWorkItemImpl());

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        WorkItem addedWorkItem = workItem.getDirectChildWorkItem("CHILD_TEST_WORK_ITEM");

        assertThat(addedWorkItem).isEqualTo(childWorkItem);
    }

    @Test
    public void testGetChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem aaa = new SimpleWorkItem("AAA");

        aa.addChildWorkItem(aaa);

        assertThat(a.getChildWorkItem("AAA")).isEqualTo(aaa);
    }

    @Test
    public void testHasDirectChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        WorkItem aa = new SimpleWorkItem("AA");

        a.addChildWorkItem(aa);

        WorkItem aaa = new SimpleWorkItem("AAA");

        aa.addChildWorkItem(aaa);

        assertThat(a.hasDirectChildWorkItem("AA")).isTrue();
        assertThat(a.hasDirectChildWorkItem("AAA")).isFalse();
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

        assertThat(a.hasChildWorkItem("AAA")).isTrue();
        assertThat(aa.hasChildWorkItem("AB")).isFalse();
    }

    @Test
    public void testNullOnNonExistingChildWorkItem() {
        WorkItem a = new SimpleWorkItem("A");

        a.setRootWorkItem(new RootWorkItemImpl());

        assertThat(a.getChildWorkItem("NON_EXISTING_WORK_ITEM")).isNull();
    }

    @Test(expected = Exception.class)
    public void testFailOnDuplicateAddingOfChildWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);
        
        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        workItem.addChildWorkItem(childWorkItem);
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

    @Test(expected = Exception.class)
    public void testFailOnRemoveNonAddedDirectChildWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.removeDirectChildWorkItem(childWorkItem.getId());
    }

    @Test
    public void testSettingUpContextForChildrenWorkItem() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);

        assertThat(childWorkItem.getParent()).isEqualTo(workItem);
        assertThat(childWorkItem.getRootWorkItem()).isEqualTo(rootWorkItem);
    }

    @Test(expected = Exception.class)
    public void testFailOnAddingToContainerWhichHasNoParent() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        workItem.addChildWorkItem(childWorkItem);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailOnAddingToContainerEarlyAddedChildWorkItem() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(childWorkItem);

        rootWorkItem.addChildWorkItem(childWorkItem);
    }

    @Test(expected = Exception.class)
    public void testFailOnAddingWorkItemToHimself() {
        WorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem workItem = new SimpleWorkItem("TEST_WORK_ITEM");

        rootWorkItem.addChildWorkItem(workItem);

        workItem.addChildWorkItem(workItem);
    }

    @Test
    public void testHasAncestor() {
        WorkItem root = new RootWorkItemImpl();

        WorkItem a = new SimpleWorkItem("A");

        root.addChildWorkItem(a);

        WorkItem b = new SimpleWorkItem("B");

        root.addChildWorkItem(b);

        assertThat(a.hasAncestor(root)).isTrue();
        assertThat(a.hasAncestor(b)).isFalse();
        assertThat(a.hasAncestor(a)).isFalse();
    }

    @Test
    public void testEventRaising() {
        EventHistory eventHistory = new EventHistory();

        final RootWorkItem rootWorkItem = new RootWorkItemImpl();

        final WorkItem childWorkItemA = new FakeEventWorkItem("CHILD_WORK_ITEM_A", eventHistory);

        final WorkItem childWorkItemAA = new FakeEventWorkItem("CHILD_WORK_ITEM_AA", eventHistory);

        final WorkItem childWorkItemAB = new FakeEventWorkItem("CHILD_WORK_ITEM_AB", eventHistory);

        final WorkItem childWorkItemB = new FakeEventWorkItem("CHILD_WORK_ITEM_B", eventHistory);

        final WorkItem childWorkItemBA = new FakeEventWorkItem("CHILD_WORK_ITEM_BA", eventHistory);

        final WorkItem childWorkItemBB = new FakeEventWorkItem("CHILD_WORK_ITEM_BB", eventHistory);

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

        assertThat(eventHistory).isEqualTo(expectedEventHistory);
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