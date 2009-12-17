package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_SUBMIT_FEEDBACK_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface FeedbackActionSite extends ActionSite {
    @ActionHandler(HELP_SUBMIT_FEEDBACK_ACTION)
    void onSendFeedback();

    @ActionPermission(HELP_SUBMIT_FEEDBACK_ACTION)
    boolean isFeedbackEnabled();
}
