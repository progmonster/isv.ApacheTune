package com.apachetune.httpserver.ui.welcomescreen;

import com.apachetune.core.ui.NView;

import java.net.URI;
import java.util.List;

/**
 * FIXDOC
 */
public interface WelcomeScreenView extends NView {
    void setRecentOpenedServerList(List<URI> serverUriList);

    void openStartPage();

    void reloadStartPage();
}
