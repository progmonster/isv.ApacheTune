package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.FILE_SAVE_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SaveFileActionSite extends ActionSite {
    @ActionHandler(FILE_SAVE_ACTION)
    void onSaveFile();

    @ActionPermission(FILE_SAVE_ACTION)
    boolean isSaveFileEnabled();
}
