package com.apachetune.httpserver.ui.actions;

import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.actions.ActionSite;

import static com.apachetune.httpserver.Constants.HELP_SHOW_NEWS_MESSAGES_ACTION;

/**
 * FIXDOC
 */
public interface NewsMessagesActionSite extends ActionSite {
    @ActionHandler(HELP_SHOW_NEWS_MESSAGES_ACTION)
    void onShowNewsMessageDialog();

    @ActionPermission(HELP_SHOW_NEWS_MESSAGES_ACTION)
    boolean isShownNewsMessageDialogActionEnabled();
}
