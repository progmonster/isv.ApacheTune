package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.WINDOW_SELECT_NEXT_TAB_ACTION;
import static com.apachetune.core.ui.Constants.WINDOW_SELECT_PREVIOUS_TAB_ACTION;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface WindowActionSite extends ActionSite {
    @ActionHandler(WINDOW_SELECT_NEXT_TAB_ACTION)
    void onNextWindowTab();

    @ActionPermission(WINDOW_SELECT_NEXT_TAB_ACTION)
    boolean isNextWindowTabEnabled();

    @ActionHandler(WINDOW_SELECT_PREVIOUS_TAB_ACTION)
    void onPreviousWindowTab();

    @ActionPermission(WINDOW_SELECT_PREVIOUS_TAB_ACTION)
    boolean isPreviousWindowTabEnabled();
}
