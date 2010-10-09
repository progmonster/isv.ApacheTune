package com.apachetune.httpserver.ui.welcomescreen;

import javax.swing.*;
import java.net.URI;
import java.util.List;

/**
 * FIXDOC
 */
public interface WelcomeScreen {
    JPanel getMainPanel();

    void setRecentOpenedServerList(List<URI> serverUriList);
}
