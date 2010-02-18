package com.apachetune.core.ui.impl;

import com.google.inject.*;
import com.apachetune.core.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ToolBarManagerImpl implements ToolBarManager {
    private final JFrame mainFrame;

    private final JToolBar toolBar;

    private final CoreUIUtils coreUIUtils;

    private final Map<String, Component> actionGroups = new HashMap<String, Component>();

    @Inject
    public ToolBarManagerImpl(JToolBar toolBar, JFrame mainFrame, CoreUIUtils coreUIUtils) {
        this.toolBar = toolBar;
        this.mainFrame = mainFrame;
        this.coreUIUtils = coreUIUtils;
    }

    public void initialize() {
        toolBar.setBorder(BorderFactory.createEtchedBorder());

        toolBar.setFloatable(false);

        mainFrame.add(toolBar, BorderLayout.PAGE_START);
    }

    public void addActionGroup(String groupId, com.apachetune.core.ui.actions.Action... actions) {
        if (groupId == null) {
            throw new NullPointerException("Argument groupId cannot be a null [this = " + this + "]");
        }

        if (groupId.isEmpty()) {
            throw new IllegalArgumentException("Argument groupId cannot be empty [this = " + this + "]");
        }

        if (actionGroups.containsKey(groupId)) {
            throw new IllegalArgumentException("The group already added [groupId = \"" + groupId + "\"; this = " + this
                    + "]");
        }

        if (actions.length == 0) {
            throw new IllegalArgumentException("Argument actions must contains at least one action [actions = " +
                    Arrays.toString(actions) + "; this = " + this + "]");
        }

        addActionGroupInternal(groupId, -1, actions);
    }

    public void addActionGroupAfter(String groupId, String afterGroupId, com.apachetune.core.ui.actions.Action... actions) {
        if (groupId == null) {
            throw new NullPointerException("Argument groupId cannot be a null [this = " + this + "]");
        }

        if (groupId.isEmpty()) {
            throw new IllegalArgumentException("Argument groupId cannot be empty [this = " + this + "]");
        }

        if (actionGroups.containsKey(groupId)) {
            throw new IllegalArgumentException("The group already added [groupId = \"" + groupId + "\"; this = " + this
                    + "]");
        }

        if (actions.length == 0) {
            throw new IllegalArgumentException("Argument actions must contains at least one action [this = " + this +
                    "]");
        }

        if (afterGroupId == null) {
            throw new NullPointerException("Argument afterGroupId cannot be a null [this = " + this + "]");
        }

        if (afterGroupId.isEmpty()) {
            throw new IllegalArgumentException("Argument afterGroupId cannot be empty [this = " + this + "]");
        }

        if (!actionGroups.containsKey(afterGroupId)) {
            throw new IllegalArgumentException("The group after that it will be added not found [afterGroupId = " +
                    afterGroupId + ";" + " this = " + this + "]");
        }

        Component afterComponent = actionGroups.get(afterGroupId);

        int afterIdx = toolBar.getComponentIndex(afterComponent);

        addActionGroupInternal(groupId, afterIdx, actions);
    }

    public void addToActionGroup(String actionGroupId, com.apachetune.core.ui.actions.Action... actions) {
        if (actionGroupId == null) {
            throw new NullPointerException("Argument actionGroupId cannot be a null [this = " + this + "]");
        }

        if (actionGroupId.isEmpty()) {
            throw new IllegalArgumentException("Argument actionGroupId cannot be empty [this = " + this + "]");
        }

        if (!actionGroups.containsKey(actionGroupId)) {
            throw new IllegalArgumentException("The group not exists [actionGroupId = \"" + actionGroupId + "\"; this" +
                    " = " + this + "]");
        }

        if (actions.length == 0) {
            throw new IllegalArgumentException("Argument actions must contains at least one action [this = " + this +
                    "]");
        }

        Component lastComponentInGroup = actionGroups.get(actionGroupId);

        int insertIdx = toolBar.getComponentIndex(lastComponentInGroup);

        while ((insertIdx > 0) && !(toolBar.getComponent(insertIdx) instanceof JToolBar.Separator)) {
            --insertIdx;
        }

        for (int actionIdx = actions.length - 1; actionIdx >= 0; actionIdx--) {
            com.apachetune.core.ui.actions.Action action = actions[actionIdx];

            JButton toolBarButton = new JButton(action);

            toolBarButton.setHideActionText(true);
            toolBarButton.setToolTipText(action.getShortDescription());

            coreUIUtils.addUIActionHint(toolBarButton);

            toolBar.add(toolBarButton, insertIdx);
        }
    }

    private void addActionGroupInternal(String groupId, int afterIdx, com.apachetune.core.ui.actions.Action... actions) {
        if ((afterIdx > 0) || ((afterIdx == -1) && (toolBar.getComponentCount() > 0))) {
            JSeparator separator = new JToolBar.Separator();

            if (afterIdx == -1) {
                toolBar.add(separator);
            } else {
                toolBar.add(separator, afterIdx + 1);
            }

            afterIdx = toolBar.getComponentIndex(separator);
        }

        for (int actionIdx = actions.length - 1; actionIdx >= 0; actionIdx--) {
            com.apachetune.core.ui.actions.Action action = actions[actionIdx];

            JButton toolBarButton = new JButton(action);

            toolBarButton.setRequestFocusEnabled(false);

            toolBarButton.setHideActionText(true);
            toolBarButton.setToolTipText(action.getShortDescription());

            toolBarButton.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    System.out.println(e.isTemporary());

                }

                public void focusLost(FocusEvent e) {
                    // No-op.
                }
            });

            coreUIUtils.addUIActionHint(toolBarButton);

            if (actionIdx == actions.length - 1) {
                actionGroups.put(groupId, toolBarButton);
            }

            if (afterIdx == -1) {
                toolBar.add(toolBarButton);
            } else {
                toolBar.add(toolBarButton, afterIdx + 1);
            }
        }
    }
}
