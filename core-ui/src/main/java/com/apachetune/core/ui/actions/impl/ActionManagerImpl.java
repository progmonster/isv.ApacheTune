package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;

import java.beans.*;
import java.util.ArrayList;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionManagerImpl implements ActionManager, ActionGroupListener, PropertyChangeListener {
    private final Map<String, ActionGroup> actionGroups = new LinkedHashMap<String, ActionGroup>();

    private final Map<String, Action> actions = new HashMap<String, Action>();

    private final Collection<Action> actionsToBeChangedByManager = new ArrayList<Action>();

    private final List<Object> actionSiteObjects = new ArrayList<Object>();

    public ActionGroup createActionGroup(String actionGroupId) {

        if (actionGroupId == null) {
            throw new NullPointerException("Argument actionGroupId cannot be a null [this = " + this + "]");
        }
        
        return new ActionGroupImpl(actionGroupId);
    }

    public Action createAction(String actionId, Class<? extends ActionSite> actionSiteClass) {
        if (actionId == null) {
            throw new NullPointerException("Argument actionId cannot be a null [this = " + this + "]");
        }

        if (actionSiteClass == null) {
            throw new NullPointerException("Argument actionSiteClass cannot be a null [this = " + this + "]");
        }

        return new ActionImpl(actionId, actionSiteClass);
    }

    public void registerActionGroup(ActionGroup actionGroup) {
        if (actionGroup == null) {
            throw new NullPointerException("Argument actionGroup cannot be a null [this = " + this + "]");
        }

        ensureActionGroupNotYetWasAdded(actionGroup);

        actionGroups.put(actionGroup.getId(), actionGroup);

        actionGroup.addListener(this);

        registerActionsInGroup(actionGroup);        
    }

    public void unregisterActionGroup(ActionGroup actionGroup) {
        if (actionGroup == null) {
            throw new NullPointerException("Argument actionGroup cannot be a null [this = " + this + ']');
        }

        if (!actionGroups.containsKey(actionGroup.getId())) {
            throw new IllegalArgumentException("Action group not contains in the action manager [actionGroup = " +
                    actionGroup + "; this = " + this + ']');
        }

        unregisterActionsInGroup(actionGroup);

        actionGroup.removeListener(this);

        actionGroups.remove(actionGroup.getId());
    }

    public void unregisterActionGroup(String actionGroupId) {
        if (actionGroupId == null) {
            throw new NullPointerException("Argument actionGroupId cannot be a null [this = " + this + "]");
        }

        ensureActionGroupWasAdded(actionGroupId);

        unregisterActionGroup(actionGroups.get(actionGroupId));
    }

    public Collection<ActionGroup> getActionGroups() {
        return Collections.unmodifiableCollection(actionGroups.values());
    }

    public ActionGroup getActionGroup(String actionGroupId) {
        if (actionGroupId == null) {
            throw new NullPointerException("Argument actionGroupId cannot be a null [this = " + this + "]");
        }
        
        ensureActionGroupWasAdded(actionGroupId);

        return actionGroups.get(actionGroupId);
    }

    public Action getAction(String actionId) {
        if (actionId == null) {
            throw new NullPointerException("Argument actionId cannot be a null [this = " + this + "]");
        }

        enusureActionRegistered(actionId);

        return actions.get(actionId);
    }

    public void activateActionSites(Object actionSiteObject) {
        if (actionSiteObject == null) {
            throw new NullPointerException("Argument actionSiteObject cannot be a null [this = " + this + "]");
        }

        if (!actionSiteObjects.contains(actionSiteObject)) {
            actionSiteObjects.add(actionSiteObject);
        }

        for (Action action : actions.values()) {
            if (action.getActionSiteClass().isInstance(actionSiteObject)) {
                setActionSiteObjectForAction(action, actionSiteObject);
            }
        }
    }

    public void deactivateActionSites(Object actionSiteObject) {
        if (actionSiteObject == null) {
            throw new NullPointerException("Argument actionSiteObject cannot be a null [this = " + this + "]");
        }

        if (!actionSiteObjects.contains(actionSiteObject)) {
            return;
        }

        for (Action action : actions.values()) {
            if (actionSiteObject.equals(action.getActionSite())) {
                setActionSiteObjectForAction(action, null);
            }
        }

        actionSiteObjects.remove(actionSiteObject);
    }

    public void updateActionSites(Object actionSiteObject) {
        if (actionSiteObject == null) {
            return;
        }

        if (!actionSiteObjects.contains(actionSiteObject)) {
            return;
        }

        for (Action action : actions.values()) {
            if (actionSiteObject.equals(action.getActionSite())) {
                action.update();
            }
        }
    }

    public void onActionAdded(ActionGroup actionGroup, Action action) {
        enusureActionNotRegisteredYet(action);

        if (action == null) {
            throw new NullPointerException("Argument action cannot be a null [this = " + this + "]");
        }

        registerAction(action);
    }

    public void onActionRemoved(ActionGroup actionGroup, Action action) {
        if (action == null) {
            throw new NullPointerException("Argument action cannot be a null [this = " + this + "]");
        }

        unregisterAction(action);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();

            if (evt.getPropertyName().equals("actionSite") && !actionsToBeChangedByManager.contains(action)) {
                throw new IllegalStateException("Action should has no change action site property while one" +
                        " registered in action manager [action = " + evt.getSource() + "; oldValue = " + evt
                        .getOldValue() + "; newValue = " + evt.getNewValue() + "; this = " + this + "]");
            }
        }
    }

    private void enusureActionNotRegisteredYet(Action action) {
        if (actions.containsKey(action.getId())) {
            throw new IllegalStateException("Action already contains in action manager [action = " + action +
                    "; this = " + this + "]");
        }
    }

    private void enusureActionRegistered(String actionId) {
        if (!actions.containsKey(actionId)) {
            throw new IllegalStateException("Action not contains in action manager [actionId = " + actionId +
                    "; this = " + this + "]");
        }
    }

    private void registerActionsInGroup(ActionGroup actionGroup) {
        for (Action action : actionGroup.getActions()) {
            registerAction(action);
        }
    }

    private void registerAction(Action action) {
        enusureActionNotRegisteredYet(action);

        if (action.getActionSite() != null) {
            throw new IllegalStateException("Action should has a null active site property [action = " + action +
                    "this = " + this + "]");
        }

        actions.put(action.getId(), action);

        action.addPropertyChangeListener(this);

        ListIterator actionSiteObjectsIter = actionSiteObjects.listIterator(actionSiteObjects.size());
        
        while (actionSiteObjectsIter.hasPrevious()) {
            Object actionSiteObject = actionSiteObjectsIter.previous();

            if (action.getActionSiteClass().isInstance(actionSiteObject)) {
                setActionSiteObjectForAction(action, actionSiteObject);

                break;
            }
        }
    }

    private void unregisterActionsInGroup(ActionGroup actionGroup) {
        for (Action action : actionGroup.getActions()) {
            unregisterAction(action);
        }
    }

    private void unregisterAction(Action action) {
        setActionSiteObjectForAction(action, null);

        action.removePropertyChangeListener(this);

        actions.remove(action.getId());
    }

    private void setActionSiteObjectForAction(Action action, final Object actionSiteObject) {
        new ActionChangeEnableSection() {
            protected void doExecute(Action action) {
                action.setActionSite((ActionSite) actionSiteObject);
            }
        }.execute(action);
    }

    private void ensureActionGroupNotYetWasAdded(ActionGroup actionGroup) {
        if (actionGroups.containsKey(actionGroup.getId())) {
            throw new IllegalArgumentException("Action group already contains in the action manager [actionGroup = " +
                    actionGroup + "; this = " + this + ']');
        }
    }

    private void ensureActionGroupWasAdded(String actionGroupId) {
        if (!actionGroups.containsKey(actionGroupId)) {
            throw new IllegalArgumentException("Action group not contains in the action manager [actionGroupId = " +
                    actionGroupId + "; this = " + this + ']');
        }
    }

    private abstract class ActionChangeEnableSection {

        public final void execute(Action action) {
            actionsToBeChangedByManager.add(action);

            try {
                doExecute(action);
            } finally {
                actionsToBeChangedByManager.remove(action);
            }
        }

        protected abstract void doExecute(Action action);

    }
}
