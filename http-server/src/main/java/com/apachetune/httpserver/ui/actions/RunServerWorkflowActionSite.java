package com.apachetune.httpserver.ui.actions;

import com.apachetune.core.ui.actions.*;
import static com.apachetune.httpserver.Constants.SERVER_START_HTTP_SERVER_ACTION;
import static com.apachetune.httpserver.Constants.SERVER_RESTART_HTTP_SERVER_ACTION;
import static com.apachetune.httpserver.Constants.SERVER_STOP_HTTP_SERVER_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
