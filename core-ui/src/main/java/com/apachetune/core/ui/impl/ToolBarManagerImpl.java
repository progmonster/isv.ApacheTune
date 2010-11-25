package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.CoreUIUtils;
import com.apachetune.core.ui.ToolBarManager;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
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
        notEmpty(groupId, "Argument groupId cannot be empty");

        isTrue(!actionGroups.containsKey(groupId),
                "The group already added [groupId = \"" + groupId + "\"; this = " + this + "]");

        isTrue(actions.length != 0, "Argument actions must contains at least one action [actions = " +
                    Arrays.toString(actions) + "; this = " + this + "]");

        addActionGroupInternal(groupId, -1, actions);
    }

    public void addActionGroupAfter(String groupId, String afterGroupId, com.apachetune.core.ui.actions.Action... actions) {
        notEmpty(groupId, "Argument groupId cannot be empty");

        isTrue(!actionGroups.containsKey(groupId),
                "The group already added [groupId = \"" + groupId + "\"; this = " + this + "]");

        isTrue(actions.length != 0, "Argument actions must contains at least one action");

        notNull(afterGroupId, "Argument afterGroupId cannot be a null");

        isTrue(!afterGroupId.isEmpty(), "Argument afterGroupId cannot be empty");

        isTrue(actionGroups.containsKey(afterGroupId),
                "The group after that it will be added not found [afterGroupId = " + afterGroupId + ";" + " this = " +
                        this + "]");

        Component afterComponent = actionGroups.get(afterGroupId);

        int afterIdx = toolBar.getComponentIndex(afterComponent);

        addActionGroupInternal(groupId, afterIdx, actions);
    }

    public void addToActionGroup(String actionGroupId, com.apachetune.core.ui.actions.Action... actions) {
        notEmpty(actionGroupId, "Argument actionGroupId cannot be empty");

        isTrue(actionGroups.containsKey(actionGroupId),
                "The group not exists [actionGroupId = \"" + actionGroupId + "\"; this" + " = " + this + "]");

        isTrue(actions.length != 0, "Argument actions must contains at least one action");

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
