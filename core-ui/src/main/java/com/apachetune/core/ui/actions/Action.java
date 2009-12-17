package com.apachetune.core.ui.actions;

import javax.swing.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface Action extends javax.swing.Action, Cloneable {
    String getId();

    ActionGroup getActionGroup();

    Class<? extends ActionSite> getActionSiteClass();

    ActionSite getActionSite();

    void setActionSite(ActionSite actionSite);

    void update();

    String getName();

    void setName(String name);

    ImageIcon getSmallIcon();

    void setSmallIcon(ImageIcon smallIcon);

    ImageIcon getLargeIcon();

    void setLargeIcon(ImageIcon largeIcon);

    String getShortDescription();

    void setShortDescription(String shortDescription);

    String getLongDescription();

    void setLongDescription(String longDescription);

    int getMnemonicKey();

    void setMnemonicKey(char mnemonicKey);

    KeyStroke getAcceleratorKey();

    void setAcceleratorKey(KeyStroke keyStroke);

    void setShowInContextMenu(boolean showInContextMenu);

    boolean canShowInContextMenu();

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    Action clone();
}
