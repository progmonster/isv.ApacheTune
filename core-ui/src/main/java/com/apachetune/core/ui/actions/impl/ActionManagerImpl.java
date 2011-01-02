package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionManagerImpl implements ActionManager, ActionGroupListener, PropertyChangeListener {
    private final Map<String, ActionGroup> actionGroups = new LinkedHashMap<String, ActionGroup>();

    private final Map<String, Action> actions = new HashMap<String, Action>();

    private final Collection<Action> actionsToBeChangedByManager = new ArrayList<Action>();

    private final List<Object> actionSiteObjects = new ArrayList<Object>();

    public ActionGroup createActionGroup(String actionGroupId) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionGroupId == null, "Argument actionGroupId cannot be a null"); //NON-NLS

        return new ActionGroupImpl(actionGroupId);
    }

    public Action createAction(String actionId, Class<? extends ActionSite> actionSiteClass) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionId, "Argument actionId cannot be a null"); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        notNull(actionSiteClass, "Argument actionSiteClass cannot be a null"); //NON-NLS

        return new ActionImpl(actionId, actionSiteClass);
    }

    public void registerActionGroup(ActionGroup actionGroup) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionGroup, "Argument actionGroup cannot be a null"); //NON-NLS

        ensureActionGroupNotYetWasAdded(actionGroup);

        actionGroups.put(actionGroup.getId(), actionGroup);

        actionGroup.addListener(this);

        registerActionsInGroup(actionGroup);        
    }

    public void unregisterActionGroup(ActionGroup actionGroup) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionGroup, "Argument actionGroup cannot be a null"); //NON-NLS

        isTrue(actionGroups.containsKey(actionGroup.getId()),
                "Action group not contains in the action manager [actionGroup = " + actionGroup + //NON-NLS
                        "; this = " + this + ']'); //NON-NLS

        unregisterActionsInGroup(actionGroup);

        actionGroup.removeListener(this);

        actionGroups.remove(actionGroup.getId());
    }

    public void unregisterActionGroup(String actionGroupId) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionGroupId == null, "Argument actionGroupId cannot be a null"); //NON-NLS

        ensureActionGroupWasAdded(actionGroupId);

        unregisterActionGroup(actionGroups.get(actionGroupId));
    }

    public Collection<ActionGroup> getActionGroups() {
        return Collections.unmodifiableCollection(actionGroups.values());
    }

    public ActionGroup getActionGroup(String actionGroupId) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionGroupId == null, "Argument actionGroupId cannot be a null"); //NON-NLS

        ensureActionGroupWasAdded(actionGroupId);

        return actionGroups.get(actionGroupId);
    }

    public Action getAction(String actionId) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionId, "Argument actionId cannot be a null"); //NON-NLS

        ensureActionRegistered(actionId);

        return actions.get(actionId);
    }

    public void activateActionSites(Object actionSiteObject) {
        //noinspection DuplicateStringLiteralInspection
        notNull(actionSiteObject, "Argument actionSiteObject cannot be a null"); //NON-NLS

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
        //noinspection DuplicateStringLiteralInspection
        notNull(actionSiteObject, "Argument actionSiteObject cannot be a null"); //NON-NLS

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
        ensureActionNotRegisteredYet(action);

        //noinspection DuplicateStringLiteralInspection
        notNull(action, "Argument action cannot be a null"); //NON-NLS

        registerAction(action);
    }

    public void onActionRemoved(ActionGroup actionGroup, Action action) {
        //noinspection DuplicateStringLiteralInspection
        notNull(action, "Argument action cannot be a null"); //NON-NLS

        unregisterAction(action);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();

            //noinspection DuplicateStringLiteralInspection
            isTrue(!evt.getPropertyName().equals("actionSite") //NON-NLS
                    || actionsToBeChangedByManager.contains(action),
                    "Action should has no change action site property while one" + //NON-NLS
                            " registered in action manager [action = " + evt.getSource() + "; oldValue = " + //NON-NLS
                            evt.getOldValue() + "; newValue = " + evt.getNewValue() //NON-NLS
                            + "; this = " + this + "]"); //NON-NLS
        }
    }

    private void ensureActionNotRegisteredYet(Action action) {
        isTrue(!actions.containsKey(action.getId()),
                "Action already contains in action manager [action = " + action + "; this = " + this + "]"); //NON-NLS
    }

    private void ensureActionRegistered(String actionId) {
        isTrue(actions.containsKey(actionId),
                "Action not contains in action manager [actionId = " + actionId + "; this = " + this + "]"); //NON-NLS
    }

    private void registerActionsInGroup(ActionGroup actionGroup) {
        for (Action action : actionGroup.getActions()) {
            registerAction(action);
        }
    }

    private void registerAction(Action action) {
        ensureActionNotRegisteredYet(action);

        isTrue(action.getActionSite() == null, "Action should has a null active site property [action = " //NON-NLS
                + action + "this = " + this + "]"); //NON-NLS

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
        isTrue(!actionGroups.containsKey(actionGroup.getId()),
                "Action group already contains in the action manager [actionGroup = " + actionGroup //NON-NLS
                        + "; this = " + this + ']'); //NON-NLS
    }

    private void ensureActionGroupWasAdded(String actionGroupId) {
        isTrue(actionGroups.containsKey(actionGroupId),
                "Action group not contains in the action manager [actionGroupId = " + actionGroupId + //NON-NLS
                        "; this = " + this + ']'); //NON-NLS
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
