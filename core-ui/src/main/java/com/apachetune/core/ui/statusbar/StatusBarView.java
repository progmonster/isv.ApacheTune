package com.apachetune.core.ui.statusbar;

import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface StatusBarView {
    void initialize();

    void setMainMessage(String message);

    void setCursorPositionState(Point position);

    void addStatusBarSite(StatusBarSite site);

    void removeStatusBarSite(StatusBarSite site);
}
