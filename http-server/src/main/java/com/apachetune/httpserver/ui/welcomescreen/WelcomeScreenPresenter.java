package com.apachetune.httpserver.ui.welcomescreen;

import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.CoreUIWorkItem;
import com.apachetune.core.ui.Presenter;
import com.apachetune.httpserver.RecentOpenedServerListChangedListener;
import com.apachetune.httpserver.RecentOpenedServersManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.httpserver.Constants.SERVER_SEARCH_FOR_HTTP_SERVER_EVENT;
import static com.apachetune.httpserver.Constants.SERVER_SELECT_HTTP_SERVER_EVENT;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 */
public class WelcomeScreenPresenter implements Presenter<WelcomeScreen>, RecentOpenedServerListChangedListener {
    private final CoreUIWorkItem coreUIWorkItem;

    private final RecentOpenedServersManager recentOpenedServerList;

    private WorkItem workItem;

    private WelcomeScreen view;

//    private final HttpServerManager httpServerManager;

    @Inject
    public WelcomeScreenPresenter(final @Named(CORE_UI_WORK_ITEM) WorkItem coreUIWorkItem,
                                  final RecentOpenedServersManager recentOpenedServerList) {
        this.coreUIWorkItem = (CoreUIWorkItem) coreUIWorkItem;
        this.recentOpenedServerList = recentOpenedServerList;
    }

    @Override
    public void initialize(WorkItem workItem, WelcomeScreen view) {
        notNull(workItem, "[this=" + this + ']');
        notNull(view, "[this=" + this + ']');

        this.workItem = workItem;
        this.view = view;

        coreUIWorkItem.switchToWelcomeScreen(view.getMainPanel());

        recentOpenedServerList.addServerListChangedListener(this);

        setRecentServerListToVIew();
    }

    @Override
    public void dispose() {
        recentOpenedServerList.removeServerListChangedListener(this);
                
        coreUIWorkItem.switchToToolWindowManager();
    }

    @Override
    public void onRecentOpenedServerListChanged() {
        setRecentServerListToVIew();
    }

    // todo on server path selected handler
    // workItem.raiseEvent(Constants.SERVER_PATH_SELECTED_EVENT, view.getPath());

    public void OnShowOpenServerDialog() {
        workItem.raiseEvent(SERVER_SELECT_HTTP_SERVER_EVENT);
    }

    public void OnShowSearchServerDialog() {
        workItem.raiseEvent(SERVER_SEARCH_FOR_HTTP_SERVER_EVENT);
    }

    private void setRecentServerListToVIew() {
        view.setRecentOpenedServerList(recentOpenedServerList.getServerUriList());
    }
}
