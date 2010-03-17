package com.apachetune.httpserver.ui.actions;

import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.actions.ActionSite;

import static com.apachetune.httpserver.Constants.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface RunServerWorkflowActionSite extends ActionSite {
    @ActionHandler(SERVER_START_HTTP_SERVER_ACTION)
    void onServerStart();

    @ActionPermission(SERVER_START_HTTP_SERVER_ACTION)
    boolean isServerStartEnabled();

    @ActionHandler(SERVER_STOP_HTTP_SERVER_ACTION)
    void onServerStop();

    @ActionPermission(SERVER_STOP_HTTP_SERVER_ACTION)
    boolean isServerStopEnabled();

    @ActionHandler(SERVER_RESTART_HTTP_SERVER_ACTION)
    void onServerRestart();

    @ActionPermission(SERVER_RESTART_HTTP_SERVER_ACTION)
    boolean isServerRestartEnabled();
}
