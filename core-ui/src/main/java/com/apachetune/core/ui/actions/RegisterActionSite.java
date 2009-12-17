package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_REGISTER_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface RegisterActionSite extends ActionSite {
    @ActionHandler(HELP_REGISTER_ACTION)
    void onAppRegister();

    @ActionPermission(HELP_REGISTER_ACTION)
    boolean isAppRegisterEnabled();
}
