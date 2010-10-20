package com.apachetune.httpserver.ui.welcomescreen;

import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.CoreUIWorkItem;
import com.apachetune.core.ui.NPresenter;
import com.apachetune.httpserver.RecentOpenedServerListChangedListener;
import com.apachetune.httpserver.RecentOpenedServersManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.httpserver.Constants.SERVER_SEARCH_FOR_HTTP_SERVER_EVENT;
import static com.apachetune.httpserver.Constants.SERVER_SELECT_HTTP_SERVER_EVENT;

/**
 * FIXDOC
 */
public class WelcomeScreenPresenter extends NPresenter<WelcomeScreenView>
        implements RecentOpenedServerListChangedListener {
    private final CoreUIWorkItem coreUIWorkItem;

    private final RecentOpenedServersManager recentOpenedServersManager;

//    private final HttpServerManager httpServerManager;

    @Inject
    public WelcomeScreenPresenter(final @Named(CORE_UI_WORK_ITEM) WorkItem coreUIWorkItem,
                                  final RecentOpenedServersManager recentOpenedServersManager) {
        this.coreUIWorkItem = (CoreUIWorkItem) coreUIWorkItem;
        this.recentOpenedServersManager = recentOpenedServersManager;
    }

    @Override
    public void onViewReady() {
        recentOpenedServersManager.addServerListChangedListener(this);

        doSetRecentServerListToView();
        getView().openStartPage();
        getView().reloadStartPage();
    }

    @Override
    public void onCloseView() {
        recentOpenedServersManager.removeServerListChangedListener(this);

        coreUIWorkItem.switchToToolWindowManager();
    }

    @Override
    public void onRecentOpenedServerListChanged() {
        doSetRecentServerListToView();

        getView().reloadStartPage();
    }

    // todo on server path selected handler
    // workItem.raiseEvent(Constants.SERVER_PATH_SELECTED_EVENT, view.getPath());

    public void OnShowOpenServerDialog() {
        getWorkItem().raiseEvent(SERVER_SELECT_HTTP_SERVER_EVENT);
    }

    public void OnShowSearchServerDialog() {
        getWorkItem().raiseEvent(SERVER_SEARCH_FOR_HTTP_SERVER_EVENT);
    }

    private void doSetRecentServerListToView() {
        getView().setRecentOpenedServerList(recentOpenedServersManager.getServerUriList());
    }
}

