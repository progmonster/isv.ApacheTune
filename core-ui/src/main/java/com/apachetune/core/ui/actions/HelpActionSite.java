package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_HELP_TOPICS_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface HelpActionSite extends ActionSite {
    @ActionHandler(HELP_HELP_TOPICS_ACTION)
    void onShowHelpTopics();

    @ActionPermission(HELP_HELP_TOPICS_ACTION)
    boolean isShowHelpTopicsEnabled();
}
