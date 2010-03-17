package com.apachetune.httpserver.ui.actions;

import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.actions.ActionSite;

import static com.apachetune.core.ui.Constants.SERVER_REOPEN_SERVER_ACTION;
import static com.apachetune.core.ui.Constants.SERVER_REOPEN_SERVER_CLEAR_LIST_ACTION;
import static com.apachetune.httpserver.Constants.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SelectServerWorkflowActionSite extends ActionSite {
    @ActionHandler(SERVER_SELECT_HTTP_SERVER_ACTION)
    void onServerSelect();

    @ActionPermission(SERVER_SELECT_HTTP_SERVER_ACTION)
    boolean isServerSelectEnabled();

    @ActionHandler(SERVER_SEARCH_FOR_HTTP_SERVER_ACTION)
    void onServerSearch();

    @ActionPermission(SERVER_SEARCH_FOR_HTTP_SERVER_ACTION)
    boolean isServerSearchEnabled();

    @ActionHandler(SERVER_REOPEN_SERVER_ACTION)
    void onServerReopen();

    @ActionPermission(SERVER_REOPEN_SERVER_ACTION)
    boolean isServerReopenEnabled();

    @ActionHandler(SERVER_REOPEN_SERVER_CLEAR_LIST_ACTION)
    void onClearEarlyOpenedServerList();
    
    @ActionPermission(SERVER_REOPEN_SERVER_CLEAR_LIST_ACTION)
    boolean isClearEarlyOpenedServerListEnabled();

    @ActionHandler(SERVER_CLOSE_SERVER_ACTION)
    void onServerClose();

    @ActionPermission(SERVER_CLOSE_SERVER_ACTION)
    boolean isServerCloseEnabled();
}
