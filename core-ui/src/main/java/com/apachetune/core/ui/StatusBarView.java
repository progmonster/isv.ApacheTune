package com.apachetune.core.ui;

import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface StatusBarView {
    void initialize();

    void setMainMessage(String message);

    void setCursorPositionState(Point position);
}
