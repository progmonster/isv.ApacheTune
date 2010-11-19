package com.apachetune.core;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class StateBehaviourGenericWorkItemTest extends WorkItemAbstractTest {
    @Test
    public void testSetAndGetState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);

        assertThat(workItem.getState("FAKE_STATE")).isEqualTo(65467);
    }

    @Test
    public void testGetStateFromParent() {
        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(getRootWorkItem());

        parentWorkItem.setState("FAKE_VALUE", 556);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        assertThat(childWorkItem.getState("FAKE_VALUE")).isEqualTo(556);
    }

    @Test
    public void testOverrideState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);
        workItem.setState("FAKE_STATE", 988);

        assertThat(workItem.getState("FAKE_STATE")).isEqualTo(988);
    }


    @Test
    public void testOverrideParentState() {
        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(getRootWorkItem());

        parentWorkItem.setState("FAKE_VALUE", 556);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        childWorkItem.setState("FAKE_VALUE", 200);

        assertThat(childWorkItem.getState("FAKE_VALUE")).isEqualTo(200);
        assertThat(parentWorkItem.getState("FAKE_VALUE")).isEqualTo(200);
    }

    @Test(expected = Exception.class)
    public void testFailOnGetNonExistsState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.getState("FAKE_STATE");
    }

    @Test
    public void testHasState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        assertThat(workItem.hasState("FAKE_STATE")).isEqualTo(false);

        workItem.setState("FAKE_STATE", 65467);

        assertThat(workItem.hasState("FAKE_STATE")).isEqualTo(true);
    }

    @Test
    public void testHasParentState() {
        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(getRootWorkItem());

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        assertThat(childWorkItem.hasState("FAKE_VALUE")).isEqualTo(false);

        parentWorkItem.setState("FAKE_VALUE", 556);

        assertThat(childWorkItem.hasState("FAKE_VALUE")).isEqualTo(true);
    }

    @Test
    public void testRemoveState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);

        workItem.removeState("FAKE_STATE");

        assertThat(workItem.hasState("FAKE_STATE")).isEqualTo(false);
    }

    @Test(expected = Exception.class)
    public void testFailOnRemoveNonExistsState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.removeState("FAKE_STATE");
    }

    @Test
    public void testRemoveStateFromParent() {
        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(getRootWorkItem());

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        parentWorkItem.setState("FAKE_VALUE", 556);

        childWorkItem.removeState("FAKE_VALUE");

        assertThat(childWorkItem.hasState("FAKE_VALUE")).isEqualTo(false);
        assertThat(parentWorkItem.hasState("FAKE_VALUE")).isEqualTo(false);
    }
}
