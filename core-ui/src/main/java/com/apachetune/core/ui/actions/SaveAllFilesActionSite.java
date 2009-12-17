package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.FILE_SAVE_ALL_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SaveAllFilesActionSite extends ActionSite {
    @ActionHandler(FILE_SAVE_ALL_ACTION)
    void onSaveAllFiles();

    @ActionPermission(FILE_SAVE_ALL_ACTION)
    boolean isSaveAllFilesEnabled();
}
