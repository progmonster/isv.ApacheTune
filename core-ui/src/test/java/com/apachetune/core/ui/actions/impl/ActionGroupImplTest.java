package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.ActionGroup;
import com.apachetune.core.ui.actions.ActionGroupListener;
import com.apachetune.core.utils.BooleanValue;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.PredicateUtils.equalPredicate;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionGroupImplTest {
    @Test
    public void testAddAction() {
        Action expAction = new ActionImpl("fakeAction", FakeActionSite.class);

        ActionGroupImpl actionGroupImpl = new ActionGroupImpl("actionGroupImplToTest");

        actionGroupImpl.addAction(expAction);

        Collection<Action> actionsInTheGroup = actionGroupImpl.getActions();

        assertThat(actionsInTheGroup.size()).isEqualTo(1);

        Action action = actionsInTheGroup.iterator().next();

        assertThat(action).isEqualTo(expAction);
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

        assertThat(CollectionUtils.exists(actionsInTheGroup, equalPredicate(action2))).isFalse();
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

                assertThat(action).isEqualTo(expAction);
            }

            public void onActionRemoved(ActionGroup actionGroup, Action action) {
                // No-op.
            }
        });

        actionGroupImpl.addAction(expAction);
        
        assertThat(isRaised.value).isTrue();
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

                assertThat(action).isEqualTo(expAction);
            }
        });

        actionGroupImpl.removeAction(expAction);

        assertThat(isRaised.value).isTrue();
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
