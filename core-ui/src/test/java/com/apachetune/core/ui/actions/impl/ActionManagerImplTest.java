package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.ActionGroup;
import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionManagerImplTest {
    @Test
    public void testCreateActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        assertThat(actionGroup.getId()).isEqualTo("fakeActionGroup");
    }

    @Test
    public void testCreateAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        assertThat(action.getId()).isEqualTo("fakeAction");
        assertThat(action.getActionSiteClass()).isEqualTo(FakeActionSite.class);
    }

    @Test
    public void testRegisterActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertThat(actionGroups.size()).isEqualTo(1);
        assertThat(actionGroups.contains(expActionGroup)).isTrue();
    }

    @Test
    public void testUnregisterActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);
        actionManagerImpl.unregisterActionGroup(expActionGroup);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertThat(actionGroups.isEmpty()).isTrue();
    }

    @Test
    public void testGetActionGroup() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        ActionGroup actionGroup = actionManagerImpl.getActionGroup("fakeActionGroup");

        assertThat(actionGroup).isEqualTo(expActionGroup);
    }

    @Test(expected = Exception.class)
    public void testFailRegisterActionGroupTwice() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(expActionGroup);

        actionManagerImpl.registerActionGroup(expActionGroup);
    }

    @Test(expected = Exception.class)
    public void testFailRegisterActionTwice() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup1 = actionManagerImpl.createActionGroup("fakeActionGroup1");

        Action action1 = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup1.addAction(action1);

        ActionGroup actionGroup2 = actionManagerImpl.createActionGroup("fakeActionGroup2");

        Action action2 = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup2.addAction(action2);

        actionManagerImpl.registerActionGroup(actionGroup1);

        actionManagerImpl.registerActionGroup(actionGroup2);
    }

    @Test
    public void testGetActionGroups() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup expActionGroup1 = actionManagerImpl.createActionGroup("fakeActionGroup1");

        actionManagerImpl.registerActionGroup(expActionGroup1);

        ActionGroup expActionGroup2 = actionManagerImpl.createActionGroup("fakeActionGroup2");

        actionManagerImpl.registerActionGroup(expActionGroup2);

        Collection<ActionGroup> actionGroups = actionManagerImpl.getActionGroups();

        assertThat(actionGroups.size()).isEqualTo(2);
        assertThat(actionGroups.containsAll(asList(expActionGroup1, expActionGroup2))).isTrue();
    }

    @Test
    public void testGetAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action expAction = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(expAction);

        actionManagerImpl.registerActionGroup(actionGroup);

        Action action = actionManagerImpl.getAction("fakeAction");

        assertThat(action).isEqualTo(expAction);
    }

    @Test
    public void testEnablingActionWhenItRegisteredWithGroupAndCorrespondingActionSiteObjectWasFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isFalse();
    }

    @Test
    public void testStayActionDisabledWhenItRegisteredWithGroupAndCorrespondingActionSiteObjectWasNotFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        assertThat(action.isEnabled()).isFalse();
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

        assertThat(action.isEnabled()).isFalse();
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

        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isFalse();
    }

    @Test
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

        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isFalse();
    }

    @Test
    public void testStayActionDisabledWhenItRegisteredAndCorrespondingActionSiteObjectWasNotFound() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        actionManagerImpl.registerActionGroup(actionGroup);

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        assertThat(action.isEnabled()).isFalse();
    }

    @Test
    public void testEnablingActionOnActivateCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject(true));

        assertThat(action.isEnabled()).isTrue();
    }

    @Test
    public void testStayActionDisabledOnActivateNotCorrespondingActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new Object());

        assertThat(action.isEnabled()).isFalse();
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

        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isFalse();
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

        assertThat(action.isEnabled()).isFalse();
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
        
        assertThat(action.isEnabled()).isTrue();
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

        assertThat(action.isEnabled()).isFalse();
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
        
        assertThat(action.isEnabled()).isFalse();
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

        assertThat(action.isEnabled()).isTrue();
    }

    @Test(expected = Exception.class)
    public void testFailOnRegisterActionWithNotNullActionSiteObject() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        action.setActionSite(new FakeActionSiteObject());

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);
    }

    @Test
    public void testSetActionSiteObjectToNullInUnregisteredAction() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        actionManagerImpl.activateActionSites(new FakeActionSiteObject());

        actionGroup.removeAction(action);

        assertThat(action.getActionSite()).isNull();
    }

    @Test(expected = Exception.class)
    public void testFailOnExternalChangingOfActionSiteObjectInActionWhileOneIsRegisteredInActionManager() {
        ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

        ActionGroup actionGroup = actionManagerImpl.createActionGroup("fakeActionGroup");

        Action action = actionManagerImpl.createAction("fakeAction", FakeActionSite.class);

        actionGroup.addAction(action);

        actionManagerImpl.registerActionGroup(actionGroup);

        action.setActionSite(new FakeActionSiteObject());
    }
}
