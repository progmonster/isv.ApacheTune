package com.apachetune.httpserver.ui.actions;

import com.apachetune.core.ui.actions.*;
import static com.apachetune.httpserver.Constants.SERVER_CHECK_CONFIG_SYNTAX_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface CheckServerActionSite extends ActionSite {
    @ActionHandler(SERVER_CHECK_CONFIG_SYNTAX_ACTION)
    void onServerCheck();

    @ActionPermission(SERVER_CHECK_CONFIG_SYNTAX_ACTION)
    boolean isServerCheckEnabled();
}
