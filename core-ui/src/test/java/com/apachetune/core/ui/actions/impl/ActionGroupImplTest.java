package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;
import com.apachetune.core.utils.*;
import org.apache.commons.collections.*;
import static org.apache.commons.collections.PredicateUtils.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;

import static java.util.Arrays.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class ActionGroupImplTest {
    @Test
    public void testAddAction() {
        Action expAction = new ActionImpl("fakeAction", FakeActionSite.class);

        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        actionGroupImpl.addAction(expAction);

        Collection<Action> actionsInTheGroup = actionGroupImpl.getActions();

        assertEquals(actionsInTheGroup.size(), 1);

        Action action = actionsInTheGroup.iterator().next();

        assertEquals(action, expAction);
    }

    @Test
    public void testRemoveAction() {
        Action action1 = new ActionImpl("fakeAction1", FakeActionSite1.class);

        Action action2 = new ActionImpl("fakeAction2", FakeActionSite2.class);

        Action action3 = new ActionImpl("fakeAction3", FakeActionSite3.class);

        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        actionGroupImpl.addAction(action1);
        actionGroupImpl.addAction(action2);
        actionGroupImpl.addAction(action3);

        actionGroupImpl.removeAction(action2);

        Collection<Action> actionsInTheGroup = actionGroupImpl.getActions();

        assertFalse(CollectionUtils.exists(actionsInTheGroup, equalPredicate(action2)));
    }

    @Test
    public void testGetActions() {
        Action action1 = new ActionImpl("fakeAction1", FakeActionSite1.class);

        Action action2 = new ActionImpl("fakeAction2", FakeActionSite2.class);

        Action action3 = new ActionImpl("fakeAction3", FakeActionSite3.class);

        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        actionGroupImpl.addAction(action1);
        actionGroupImpl.addAction(action2);
        actionGroupImpl.addAction(action3);

        Collection<Action> actionsInTheGroup = actionGroupImpl.getActions();

        CollectionUtils.disjunction(actionsInTheGroup, asList(action1, action2, action3));
    }

    @Test
    public void testActionAddedEvent() {
        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        final BooleanValue isRaised = new BooleanValue();

        final Action expAction = new ActionImpl("fakeAction", FakeActionSite.class);

        actionGroupImpl.addListener(new ActionGroupListener() {
            public void onActionAdded(ActionGroup actionGroup, Action action) {
                isRaised.value = true;

                assertEquals(action, expAction);
            }

            public void onActionRemoved(ActionGroup actionGroup, Action action) {
                // No-op.
            }
        });

        actionGroupImpl.addAction(expAction);
        
        assertTrue(isRaised.value);
    }

    @Test
    public void testActionRemovedEvent() {
        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        final BooleanValue isRaised = new BooleanValue();

        final Action expAction = new ActionImpl("fakeAction", FakeActionSite.class);

        actionGroupImpl.addAction(expAction);

        actionGroupImpl.addListener(new ActionGroupListener() {
            public void onActionAdded(ActionGroup actionGroup, Action action) {
                // No-op.
            }

            public void onActionRemoved(ActionGroup actionGroup, Action action) {
                isRaised.value = true;

                assertEquals(action, expAction);
            }
        });

        actionGroupImpl.removeAction(expAction);

        assertTrue(isRaised.value);
    }

    @Test
    public void testRemoveListener() {
        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        ActionGroupListener listener = new ActionGroupListener() {
            public void onActionAdded(ActionGroup actionGroup, Action action) {
                fail();
            }

            public void onActionRemoved(ActionGroup actionGroup, Action action) {
                fail();
            }
        };
        
        actionGroupImpl.addListener(listener);

        actionGroupImpl.removeListener(listener);

        Action action = new ActionImpl("fakeAction", FakeActionSite.class);

        actionGroupImpl.addAction(action);
        actionGroupImpl.removeAction(action);
    }
}
