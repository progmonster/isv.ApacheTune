package com.apachetune.core.ui;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ToolBarManager {
    void initialize();

    void addActionGroup(String groupId, com.apachetune.core.ui.actions.Action... actions);

    void addActionGroupAfter(String groupId, String afterGroupId, com.apachetune.core.ui.actions.Action... actions);

    void addToActionGroup(String actionGroupId, com.apachetune.core.ui.actions.Action... actions);
}
