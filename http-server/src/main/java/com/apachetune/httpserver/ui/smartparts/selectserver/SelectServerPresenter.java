package com.apachetune.httpserver.ui.smartparts.selectserver;

import com.apachetune.core.*;
import com.apachetune.core.ui.*;
import com.apachetune.httpserver.Constants;
import com.apachetune.httpserver.*;
import com.google.inject.*;

import java.io.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        if (view == null) {
            throw new NullPointerException("Argument view cannot be a null [this = " + this + "]");
        }
        
        this.workItem = workItem;
        this.view = view;

        if (recentOpenedServerList.hasLastOpenedServer()) {
            view.setCurrentDir(new File(recentOpenedServerList.getLastOpenedServerUri()).getAbsolutePath());
        }
    }

    public void onDirectorySelected() {
        workItem.raiseEvent(Constants.SERVER_PATH_SELECTED_EVENT, view.getPath());
    }

    public void onCurrentDirectoryChanged(File selectedDirectory) {
        if (selectedDirectory == null) {
            throw new NullPointerException("Argument selectedDirectory cannot be a null [this = " + this + "]");
        }
        
        view.setCurrentDirectorySelectable(httpServerManager.isHttpServerRootDirectory(selectedDirectory));
    }
}
