package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_CHECK_FOR_UPDATE_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface UpdateActionSite extends ActionSite {
    @ActionHandler(HELP_CHECK_FOR_UPDATE_ACTION)
    void onCheckForUpdate();

    @ActionPermission(HELP_CHECK_FOR_UPDATE_ACTION)
    boolean isCheckForUpdateEnabled();
}
