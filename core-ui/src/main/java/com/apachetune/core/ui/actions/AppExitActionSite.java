package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.EXIT_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface AppExitActionSite extends ActionSite {
    @ActionHandler(EXIT_ACTION)
    void onAppExit();

    @ActionPermission(EXIT_ACTION)
    boolean isAppExitEnabled();
}
