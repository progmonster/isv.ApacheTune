package com.apachetune.httpserver.ui.smartparts.selectserver;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SelectServerDialog {
    void setCurrentDir(String initialPath);

    String getPath();

    void setCurrentDirectorySelectable(boolean isSelectable);
}
