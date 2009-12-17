package com.apachetune.core.ui;

import com.apachetune.core.ui.actions.*;

import javax.swing.*;
import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface MenuBarManager {
    JMenuBar getMenuBar();

    // TODO Add ability of adding menu item to groups separated from each other.
    void addMenuAfter(String menuId, JMenu menu, String afterMenuId);

    void addMenu(String menuId, JMenu menu);

    JMenu getMenu(String menuId);

    void createAndBindContextMenu(Component component, ActionSite actionSite);
}
