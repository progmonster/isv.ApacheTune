package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;

import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionGroupImpl implements ActionGroup {
    private final String id;

    private final Map<String, Action> actions = new LinkedHashMap<String, Action>();

    private final List<ActionGroupListener> actionGroupListeners = new ArrayList<ActionGroupListener>();

    public ActionGroupImpl(String id) {
        if (id == null) {
            throw new NullPointerException("Argument id cannot be a null [this = " + this + "]");
        }       

        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addAction(Action action) {
        if (action == null) {
            throw new NullPointerException("Argument action cannot be a null [this = " + this + "]");
        }

        if (actions.containsKey(action.getId())) {
            throw new IllegalArgumentException("Action group already contains an action this same id [action = " +
                    action + ";this = " + this + "]");
        }

        setActionGroupForAction(action, this);

        actions.put(action.getId(), action);

        fireActionAdded(action);
    }

    public void removeAction(Action action) {
        if (action == null) {
            throw new NullPointerException("Argument action cannot be a null [this = " + this + "]");
        }

        if (!actions.containsKey(action.getId())) {
            throw new IllegalArgumentException("Action not contains in this action group [action = " + action +
                    ";this = " + this + "]");
        }

        actions.remove(action.getId());
        setActionGroupForAction(action, null);

        fireActionRemoved(action);
    }

    public Collection<Action> getActions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    public void addListener(ActionGroupListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        actionGroupListeners.add(listener);
    }

    public void removeListener(ActionGroupListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        actionGroupListeners.remove(listener);
    }

    public void removeAllListeners() {
        actionGroupListeners.clear();
    }

    @Override
    public String toString() {
        return "ActionGroupImpl [id = " + id + ']';
    }

    private void fireActionAdded(Action action) {
        List<ActionGroupListener> listenerListCopy = new ArrayList<ActionGroupListener>(actionGroupListeners);

        for (ActionGroupListener listener : listenerListCopy) {
            listener.onActionAdded(this, action);
        }
    }

    private void fireActionRemoved(Action action) {
        List<ActionGroupListener> listenerListCopy = new ArrayList<ActionGroupListener>(actionGroupListeners);

        for (ActionGroupListener listener : listenerListCopy) {
            listener.onActionRemoved(this, action);
        }
    }

    private void setActionGroupForAction(Action action, ActionGroup actionGroup) {
        ((ActionImpl) action).setActionGroup(actionGroup);
    }
}
