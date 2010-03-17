package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.ActionGroup;
import org.testng.annotations.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class ActionManagerImplTest {
    @Test
    public void testCreateActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        assertEquals(actionGroup.getId(), "fakeActionGroup");
    }

    @Test
    public void testCreateAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        assertEquals(action.getId(), "fakeAction");
        assertEquals(action.getActionSiteClass(), FakeActionSite.class);
    }

    @Test
    public void testRegisterActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertEquals(actionGroups.size(), 1);
        assertTrue(actionGroups.contains(expActionGroup));
    }

    @Test
    public void testUnregisterActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);
        actionManagerImpl.unregisterActionGroup(expActionGroup);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertTrue(actionGroups.isEmpty());
    }

    @Test
    public void testGetActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        ActionGroup actionGroup = actionManagerImpl.getActionGroup("fakeActionGroup");

        assertEquals(actionGroup, expActionGroup);        
    }

    @Test
    public void testFailRegisterActionGroupTwice() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        try {
            actionManagerImpl.registerActionGroup(expActionGroup);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testFailRegisterActionTwice() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup1 = actionManagerImpl.createActionGroup("fakeActionGroup1");

        Action action1 = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup1.addAction(action1);

        ActionGroup actionGroup2 = actionManagerImpl.createActionGroup("fakeActionGroup2");

        Action action2 = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup2.addAction(action2);

        actionManagerImpl.registerActionGroup(actionGroup1);

        try {
            actionManagerImpl.registerActionGroup(actionGroup2);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testGetActionGroups() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup1 = actionManagerImpl.createActionGroup("fakeActionGroup1");

        actionManagerImpl.registerActionGroup(expActionGroup1);

        ActionGroup expActionGroup2 = actionManagerImpl.createActionGroup("fakeActionGroup2");

        actionManagerImpl.registerActionGroup(expActionGroup2);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertEquals(actionGroups.size(), 2);
        assertTrue(actionGroups.containsAll(asList(expActionGroup1, expActionGroup2)));
    }

    @Test
    public void testGetAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action expAction = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(expAction);

        actionManagerImpl.registerActionGroup(actionGroup);

        Action action = actionManagerImpl.getAction("fakeAction");

        assertEquals(action, expAction);
    }

    @Test
    public void testEnablingActionWhenItRegisteredWithGroupAndCorrespondingActionSiteObjectWasFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        assertTrue(action.isEnabled());
    }

    @Test
    public void testDisablingActionWhenItUnregisteredWithGroupWhenCorrespondingActionSiteObjectWasFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);
        actionManagerImpl.unregisterActionGroup(actionGroup);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionDisabledWhenItRegisteredWithGroupAndCorrespondingActionSiteObjectWasNotFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testDisableActionWhenActivateAnotherCorrespondingActionSiteObjectWithDisabledHandler() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));
        actionManagerImpl.activateActionSites(new FakeActionSiteObject(false));

        assertFalse(action.isEnabled());
    }

    @Test
    public void testEnableActionWhenActivateAnotherCorrespondingActionSiteObjectWithEnabledHandler() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(false));
        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        assertTrue(action.isEnabled());
    }

    @Test
    public void testDisableActionWhenActionSiteObjectDeactivatedWhilePreviousActionSiteObjectHasActiveHandler() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(true);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);
        actionManagerImpl.deactivateActionSites(fakeActionSiteObject);

        assertFalse(action.isEnabled());
    }

    public void testStayEnabledWhenPreviousActionSiteObjectDeactivatedButHasActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(true);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        actionManagerImpl.deactivateActionSites(fakeActionSiteObject);

        assertTrue(action.isEnabled());
    }

    @Test
    public void testOkOnRepeatedAddingActionSiteObjectAfterItsWasRemoved() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject();

        actionManagerImpl.activateActionSites(fakeActionSiteObject);
        actionManagerImpl.deactivateActionSites(fakeActionSiteObject);
        actionManagerImpl.activateActionSites(fakeActionSiteObject); // No exceptions should be thrown. 
    }

    @Test
    public void testEnablingActionWhenItRegisteredAndCorrespondingActionSiteObjectFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(actionGroup);

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        assertTrue(action.isEnabled());
    }

    @Test
    public void testDisablingActionWhenItUnregisteredWhenCorrespondingActionSiteObjectWasFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionGroup.removeAction(action);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionDisabledWhenItRegisteredAndCorrespondingActionSiteObjectWasNotFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(actionGroup);

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testEnablingActionOnActivateCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        assertTrue(action.isEnabled());
    }

    @Test
    public void testStayActionDisabledOnActivateNotCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new Object());

        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionEnabledOnDeactivateNotLastCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        Object emptyActionSiteObject = new Object();

        actionManagerImpl.activateActionSites(emptyActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.deactivateActionSites(emptyActionSiteObject);

        assertTrue(action.isEnabled());
    }

    @Test
    public void testDisablingActionOnDeactivateCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(true);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.deactivateActionSites(fakeActionSiteObject);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionDisabledWhenCorrespondingActionSiteObjectHasNoActiveHandler() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(false);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testEnablingActionWhenCorrespondingActionSiteObjectUpdatedWithEnabledHandler() {        
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(false);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        fakeActionSiteObject.setTestActionEnabled(true);

        actionManagerImpl.updateActionSites(fakeActionSiteObject);
        
        assertTrue(action.isEnabled());
    }

    @Test
    public void testDisablingActionWhenCorrespondingActionSiteObjectUpdatedWithDisabledHandler() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(true);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        fakeActionSiteObject.setTestActionEnabled(false);

        actionManagerImpl.updateActionSites(fakeActionSiteObject);

        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionDisabledWhenNotCorrespondingActionSiteObjectUpdated() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(false);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.updateActionSites(new Object());
        
        assertFalse(action.isEnabled());
    }

    @Test
    public void testStayActionEnabledWhenNotCorrespondingActionSiteObjectUpdated() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject(true);

        actionManagerImpl.activateActionSites(fakeActionSiteObject);

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.updateActionSites(new Object());

        assertTrue(action.isEnabled());
    }

    @Test
    void testFailOnRegisterActionWithNotNullActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        action.setActionSite(new FakeActionSiteObject());

        actionGroup.addAction(action);

        try {
            actionManagerImpl.registerActionGroup(actionGroup);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    void testSetActionSiteObjectToNullInUnregisteredAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject());

        actionGroup.removeAction(action);

        assertNull(action.getActionSite());
    }

    @Test
    void testFailOnExternalChangingOfActionSiteObjectInActionWhileOneIsRegisteredInActionManager() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        try {
            action.setActionSite(new FakeActionSiteObject());

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }
}
