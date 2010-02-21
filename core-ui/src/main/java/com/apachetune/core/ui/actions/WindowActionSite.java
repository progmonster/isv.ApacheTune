package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.WINDOW_SELECT_NEXT_TAB_ACTION;
import static com.apachetune.core.ui.Constants.WINDOW_SELECT_PREVIOUS_TAB_ACTION;

/**
 * Created by IntelliJ IDEA.
 * User: Aleksey
 * Date: 22.02.2010
 * Time: 1:00:21
 * To change this template use File | Settings | File Templates.
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
