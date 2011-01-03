package com.apachetune.httpserver.ui.selectserver;

import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.Presenter;
import com.apachetune.httpserver.Constants;
import com.apachetune.httpserver.HttpServerManager;
import com.apachetune.httpserver.RecentOpenedServersManager;
import com.google.inject.Inject;

import java.io.File;

import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class SelectServerPresenter implements Presenter<SelectServerDialog> {
    private WorkItem workItem;

    private SelectServerDialog view;

    private final RecentOpenedServersManager recentOpenedServerList;

    private final HttpServerManager httpServerManager;

    @Inject
    public SelectServerPresenter(RecentOpenedServersManager recentOpenedServerList,
            HttpServerManager httpServerManager) {
        this.recentOpenedServerList = recentOpenedServerList;
        this.httpServerManager = httpServerManager;
    }

    public void initialize(WorkItem workItem, SelectServerDialog view) {
        //noinspection DuplicateStringLiteralInspection
        notNull(workItem, "Argument workItem cannot be a null"); //NON-NLS

        //noinspection DuplicateStringLiteralInspection
        notNull(view, "Argument view cannot be a null"); //NON-NLS

        this.workItem = workItem;
        this.view = view;

        if (recentOpenedServerList.hasLastOpenedServer()) {
            view.setCurrentDir(new File(recentOpenedServerList.getLastOpenedServerUri()).getAbsolutePath());
        }
    }

    @Override
    public void dispose() {
        // No-op.
    }

    public void onDirectorySelected() {
        workItem.raiseEvent(Constants.SERVER_PATH_SELECTED_EVENT, view.getPath());
    }

    public void onCurrentDirectoryChanged(File selectedDirectory) {
        notNull(selectedDirectory, "Argument selectedDirectory cannot be a null"); //NON-NLS
        
        view.setCurrentDirectorySelectable(httpServerManager.isHttpServerRootDirectory(selectedDirectory));
    }
}
