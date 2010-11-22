package com.apachetune.httpserver.ui.updating;

/**
 * FIXDOC
 */
public interface HasUpdateMessageDialog {
    public static enum UpdateAction {NEED_UPDATE, SKIP_UPDATE}

    UpdateAction show(UpdateInfo updateInfo);

    boolean isUserEnableCheckForUpdate();
}
