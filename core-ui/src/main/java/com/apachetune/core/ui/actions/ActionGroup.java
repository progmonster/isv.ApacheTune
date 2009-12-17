package com.apachetune.core.ui.actions;

import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ActionGroup {
    String getId();

    void addAction(Action action);

    void removeAction(Action action);

    Collection<Action> getActions();

    void addListener(ActionGroupListener listener);

    void removeListener(ActionGroupListener listener);

    void removeAllListeners();
}
