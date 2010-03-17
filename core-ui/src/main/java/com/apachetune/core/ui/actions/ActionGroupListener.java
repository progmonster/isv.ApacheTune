package com.apachetune.core.ui.actions;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ActionGroupListener {
    void onActionAdded(ActionGroup actionGroup, Action action);

    void onActionRemoved(ActionGroup actionGroup, Action action);
}
