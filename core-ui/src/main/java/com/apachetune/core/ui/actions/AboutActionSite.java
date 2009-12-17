package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_ABOUT_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface AboutActionSite extends ActionSite {
    @ActionHandler(HELP_ABOUT_ACTION)
    void onShowAboutDialog();

    @ActionPermission(HELP_ABOUT_ACTION)
    boolean isShowAboutDialogEnabled();
}
