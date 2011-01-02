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
        //noinspection DuplicateStringLiteralInspection
        notNull(id, "Argument id cannot be a null"); //NON-NLS
                                                                                                  
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addAction(Action action) {
        //noinspection DuplicateStringLiteralInspection
        notNull(action, "Argument action cannot be a null"); //NON-NLS

        isTrue(!actions.containsKey(action.getId()), "Action group already contains an action this same id" + //NON-NLS
                " [action = " + action + ";this = " + this + "]"); //NON-NLS

        setActionGroupForAction(action, this);

        actions.put(action.getId(), action);

        fireActionAdded(action);
    }

    public void removeAction(Action action) {
        //noinspection DuplicateStringLiteralInspection
        notNull(action, "Argument action cannot be a null"); //NON-NLS

        isTrue(actions.containsKey(action.getId()), "Action not contains in this action group [action = " //NON-NLS
                + action + ";this = " + this + "]"); //NON-NLS

        actions.remove(action.getId());
        setActionGroupForAction(action, null);

        fireActionRemoved(action);
    }

    public Collection<Action> getActions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    public void addListener(ActionGroupListener listener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(listener, "Argument listener cannot be a null"); //NON-NLS

        actionGroupListeners.add(listener);
    }

    public void removeListener(ActionGroupListener listener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(listener, "Argument listener cannot be a null"); //NON-NLS

        actionGroupListeners.remove(listener);
    }

    public void removeAllListeners() {
        actionGroupListeners.clear();
    }

    @Override
    public String toString() {
        return "ActionGroupImpl [id = " + id + ']'; //NON-NLS
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
