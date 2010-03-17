package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.CoreUIUtils;
import com.apachetune.core.ui.MenuBarManager;
import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.*;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static java.util.Arrays.asList;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class MenuBarManagerImpl implements MenuBarManager {
    private final JFrame mainFrame;

    private final ActionManager actionManager;

    private final CoreUIUtils coreUIUtils;

    private final JMenuBar menuBar = new JMenuBar();

    private final Map<String, JMenu> menus = new HashMap<String, JMenu>();

    private final List<String> menuOrder = new ArrayList<String>();

    @Inject
    public MenuBarManagerImpl(JFrame mainFrame, CoreUIUtils coreUIUtils, ActionManager actionManager) {
        this.mainFrame = mainFrame;
        this.coreUIUtils = coreUIUtils;
        this.actionManager = actionManager;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void addMenuAfter(String menuId, JMenu menu, String afterMenuId) {
        if (menuId == null) {
            throw new NullPointerException("Argument menuId cannot be a null [this = " + this + "]");
        }

        if (menuId.isEmpty()) {
            throw new IllegalArgumentException("Argument menuId cannot be empty [this = " + this + "]");
        }

        if (menus.containsKey(menuId)) {
            throw new IllegalArgumentException("Menu already has been added to the menu bar manager [menuId = " +
                    menuId + "; this = " + this + "]");
        }

        if (menu == null) {
            throw new NullPointerException("Argument menu cannot be a null [this = " + this + "]");
        }

        if ((afterMenuId != null) && afterMenuId.isEmpty()) {
            throw new IllegalArgumentException("Argument afterMenuId cannot be empty [this = " + this + "]");
        }

        if ((afterMenuId != null) && !menus.containsKey(afterMenuId)) {
            throw new IllegalArgumentException("The menu not contains in the menu bar manager [afterMenuId = " +
                    afterMenuId + "; this = " + this + "]");
        }

        menus.put(menuId, menu);

        if (afterMenuId != null) {
            int afterMenuIdx = menuOrder.indexOf(afterMenuId) + 1;

            menuOrder.add(afterMenuIdx, menuId);

            menuBar.add(menu, afterMenuIdx);
        } else {
            menuOrder.add(menuId);

            menuBar.add(menu);
        }

        // TODO Workaround. Need to repaint menu.
        mainFrame.setJMenuBar(null);
        mainFrame.setJMenuBar(menuBar);
    }

    // TODO Add ability of adding menu item to groups separated from each other.
    public void addMenu(String menuId, JMenu menu) {
        addMenuAfter(menuId, menu, null);
    }

    public JMenu getMenu(String menuId) {
        if (menuId == null) {
            throw new NullPointerException("Argument menuId cannot be a null [this = " + this + "]");
        }

        if (menuId.isEmpty()) {
            throw new IllegalArgumentException("Argument menuId cannot be empty [this = " + this + "]");
        }

        if (!menus.containsKey(menuId)) {
            throw new IllegalArgumentException("The menu not contains in the menu bar manager [menuId = " +
                    menuId + "; this = " + this + "]");
        }

        return menus.get(menuId);
    }

    public void createAndBindContextMenu(Component component, ActionSite actionSite) {
        if (component == null) {
            throw new NullPointerException("Argument component cannot be a null [this = " + this + "]");
        }

        if (actionSite == null) {
            throw new NullPointerException("Argument actionSite cannot be a null [this = " + this + "]");
        }

        final ContextMenu ctxMenu = createContextMenu(actionSite);

        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showCtxMenu(evt, ctxMenu);
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showCtxMenu(evt, ctxMenu);
                }
            }

            private void showCtxMenu(MouseEvent evt, ContextMenu ctxMenu) {
                ctxMenu.update();

                ctxMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        });
    }

    private ContextMenu createContextMenu(Object actionSiteObject) {
        if (!(actionSiteObject instanceof ActionSite)) {
            return null;
        }

        Set<String> handlersIds = getHandlersIdsForActionSiteObject(actionSiteObject);

        if (handlersIds.size() == 0) {
            return null;
        }

        final Set<String> actionGroupsIds = getActionGroupsIdsByActionsIds(handlersIds);

        // Get action groups from a source action group list to preserve an action group registration order.
        Collection<ActionGroup> actionGroups = filter(actionManager.getActionGroups(), new Predicate<ActionGroup>() {
            public boolean apply(ActionGroup actionGroup) {
                return actionGroupsIds.contains(actionGroup.getId());
            }
        });

        ContextMenu ctxMenu = new ContextMenu();

        boolean isCtxMenuEmpty = true;

        boolean isGroupStarted;

        for (ActionGroup group : actionGroups) {
            isGroupStarted = true;

            for (Action action : group.getActions()) {
                if (action.canShowInContextMenu() && handlersIds.contains(action.getId())) {
                    if (!isCtxMenuEmpty && isGroupStarted) {
                        ctxMenu.addSeparator();
                    }

                    Action actionCopy = action.clone();

                    // Cloned action should not has listners of its prototype.
                    actionCopy.removePropertyChangeListener((PropertyChangeListener) actionManager);

                    actionCopy.setActionSite((ActionSite) actionSiteObject);

                    coreUIUtils.addUIActionHint(ctxMenu.add(actionCopy));

                    isCtxMenuEmpty = false;
                    isGroupStarted = false;
                }
            }
        }

        return !isCtxMenuEmpty ? ctxMenu : null;
    }


    private Set<String> getHandlersIdsForActionSiteObject(Object actionSiteObject) {
        Set<String> handlersIds = new HashSet<String>();

        List<Method> methods = asList(actionSiteObject.getClass().getDeclaredMethods());

        for (Method method : methods) {
            ActionHandler actionHandlerAnnt = method.getAnnotation(ActionHandler.class);

            if (actionHandlerAnnt != null) {
                handlersIds.add(actionHandlerAnnt.value());
            }
        }

        return handlersIds;
    }

    private Set<String> getActionGroupsIdsByActionsIds(Set<String> actionsIds) {
        Set<String> actionGroupsIds = new HashSet<String>();

        for (String actionId : actionsIds) {
            Action action = actionManager.getAction(actionId);

            ensureActionHasGroup(action);

            String actionGroupId = action.getActionGroup().getId();

            if (!actionGroupsIds.contains(actionGroupId)) {
                actionGroupsIds.add(actionGroupId);
            }
        }

        return actionGroupsIds;
    }

    private void ensureActionHasGroup(Action action) {
        if (action.getActionGroup() == null) {
            throw new IllegalStateException("Action does not contained inside a group [action = " + action +
                    "; this = " + this + ']');
        }
    }

    private class ContextMenu extends JPopupMenu {
        private final Set<Action> actions = new HashSet<Action>();

        public JMenuItem add(Action action) {
            if (action == null) {
                throw new NullPointerException("Argument action cannot be a null [this = " + this + "]");
            }

            actions.add(action);

            return super.add(action);
        }

        public void update() {
            for (Action action : actions) {
                action.update();
            }
        }
    }
}
