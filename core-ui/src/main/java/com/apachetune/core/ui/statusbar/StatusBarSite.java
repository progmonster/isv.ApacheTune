package com.apachetune.core.ui.statusbar;

import javax.swing.*;


/**
 * FIXDOC
 */
public interface StatusBarSite {
    void initialize();

    JComponent getSiteComponent();

    void dispose();
}
