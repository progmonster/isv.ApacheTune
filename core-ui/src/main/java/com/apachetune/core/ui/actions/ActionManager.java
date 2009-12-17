package com.apachetune.core.ui.actions;

import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ActionManager {
    ActionGroup createActionGroup(String actionGroupId);

    Action createAction(String actionId, Class<? extends ActionSite> actionSiteClass);

    void registerActionGroup(ActionGroup actionGroup);

    void unregisterActionGroup(ActionGroup actionGroup);

    void unregisterActionGroup(String actionGroupId);

    Collection<ActionGroup> getActionGroups();

    ActionGroup getActionGroup(String actionGroupId);

    Action getAction(String actionId);

    void activateActionSites(Object actionSiteObject);

    void deactivateActionSites(Object actionSiteObject);

    void updateActionSites(Object actionSiteObject);
}

