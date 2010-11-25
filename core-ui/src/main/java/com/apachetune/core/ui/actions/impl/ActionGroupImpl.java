package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.ActionGroup;
import com.apachetune.core.ui.actions.ActionGroupListener;

import java.util.*;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionGroupImpl implements ActionGroup {
    private final String id;

    private final Map<String, Action> actions = new LinkedHashMap<String, Action>();

    private final List<ActionGroupListener> actionGroupListeners = new ArrayList<ActionGroupListener>();

    public ActionGroupImpl(String id) {
        notNull(id, "Argument id cannot be a null");

        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addAction(Action action) {
        notNull(action, "Argument action cannot be a null");

        isTrue(!actions.containsKey(action.getId()), "Action group already contains an action this same id [action = " +
                    action + ";this = " + this + "]");

        setActionGroupForAction(action, this);

        actions.put(action.getId(), action);

        fireActionAdded(action);
    }

    public void removeAction(Action action) {
        notNull(action, "Argument action cannot be a null");

        isTrue(actions.containsKey(action.getId()), "Action not contains in this action group [action = " + action +
                    ";this = " + this + "]");

        actions.remove(action.getId());
        setActionGroupForAction(action, null);

        fireActionRemoved(action);
    }

    public Collection<Action> getActions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    public void addListener(ActionGroupListener listener) {
        notNull(listener, "Argument listener cannot be a null");

        actionGroupListeners.add(listener);
    }

    public void removeListener(ActionGroupListener listener) {
        notNull(listener, "Argument listener cannot be a null");

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
