package com.apachetune.httpserver.ui.messagesystem;

import com.apachetune.core.ui.statusbar.StatusBarSite;

/**
 * FIXDOC
 */
public interface MessageStatusBarSite extends StatusBarSite {
    void setNotificationAreaActive(boolean isActive);

    void setNotificationTip(String tip);

    void showBalloonTip(String tip);
}
