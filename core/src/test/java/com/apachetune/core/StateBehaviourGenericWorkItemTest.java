package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.FileAssert.fail;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class StateBehaviourGenericWorkItemTest {
    @Test
    public void testSetAndGetState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);

        assertEquals(workItem.getState("FAKE_STATE"), 65467);
    }

    @Test
    public void testGetStateFromParent() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(rootWorkItem);

        parentWorkItem.setState("FAKE_VALUE", 556);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        assertEquals(childWorkItem.getState("FAKE_VALUE"), 556);
    }

    @Test
    public void testOverrideState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);
        workItem.setState("FAKE_STATE", 988);

        assertEquals(workItem.getState("FAKE_STATE"), 988);
    }


    @Test
    public void testOverrideParentState() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(rootWorkItem);

        parentWorkItem.setState("FAKE_VALUE", 556);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        childWorkItem.setState("FAKE_VALUE", 200);

        assertEquals(childWorkItem.getState("FAKE_VALUE"), 200);
        assertEquals(parentWorkItem.getState("FAKE_VALUE"), 200);
    }

    @Test
    public void testFailOnGetNonExistsState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        try {
            assertEquals(workItem.getState("FAKE_STATE"), 65467);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testHasState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        assertEquals(workItem.hasState("FAKE_STATE"), false);

        workItem.setState("FAKE_STATE", 65467);

        assertEquals(workItem.hasState("FAKE_STATE"), true);        
    }

    @Test
    public void testHasParentState() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(rootWorkItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        assertEquals(childWorkItem.hasState("FAKE_VALUE"), false);

        parentWorkItem.setState("FAKE_VALUE", 556);

        assertEquals(childWorkItem.hasState("FAKE_VALUE"), true);
    }

    @Test
    public void testRemoveState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        workItem.setState("FAKE_STATE", 65467);

        workItem.removeState("FAKE_STATE");

        assertEquals(workItem.hasState("FAKE_STATE"), false);
    }

    @Test
    public void testFailOnRemoveNonExistsState() {
        WorkItem workItem = new SimpleWorkItem("TEST_WORKITEM");

        try {
            workItem.removeState("FAKE_STATE");

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testRemoveStateFromParent() {
        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        WorkItem parentWorkItem = new SimpleWorkItem("PARENT_TEST_WORKITEM");

        parentWorkItem.setRootWorkItem(rootWorkItem);

        WorkItem childWorkItem = new SimpleWorkItem("CHILD_TEST_WORKITEM");

        parentWorkItem.addChildWorkItem(childWorkItem);

        parentWorkItem.setState("FAKE_VALUE", 556);

        childWorkItem.removeState("FAKE_VALUE");

        assertEquals(childWorkItem.hasState("FAKE_VALUE"), false);
        assertEquals(parentWorkItem.hasState("FAKE_VALUE"), false);
    }
}
