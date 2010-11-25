package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.core.ui.statusbar.StatusBarSite;
import com.apachetune.core.ui.statusbar.StatusBarView;
import com.google.inject.Inject;

import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.Validate.notEmpty;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class StatusBarManagerImpl implements StatusBarManager {
    // TODO Create and return an identity object after a new message adding. Remove messages from status bar nor by id,
    // but by the identity objects.

    private final StatusBarView statusBarView;

    private final List<AbstractMap.SimpleImmutableEntry<String, String>> messages = new ArrayList<AbstractMap
            .SimpleImmutableEntry<String, String>>();

    @Inject
    public StatusBarManagerImpl(StatusBarView statusBarView) {
        this.statusBarView = statusBarView;
    }

    public void initialize() {
        statusBarView.initialize();
    }

    @SuppressWarnings({"unchecked"})
    public void addMainStatus(String messageId, String status) {
        notEmpty(messageId, "Argument messageId cannot be empty");

        notEmpty(status, "Argument status cannot be empty");

        messages.add(new AbstractMap.SimpleImmutableEntry<String, String>(messageId, status));

        statusBarView.setMainMessage(status);
    }

    public void removeMainStatus(String messageId) {
        notEmpty(messageId, "Argument messageId cannot be empty");

        removeLastById(messageId);

        if (!messages.isEmpty()) {
            statusBarView.setMainMessage(messages.get(messages.size() - 1).getValue());
        } else {
            statusBarView.setMainMessage("");
        }
    }

    public void setCaretPositionState(Point position) {
        statusBarView.setCursorPositionState(position);
    }

    @Override
    public void addStatusBarSite(StatusBarSite site) {
        statusBarView.addStatusBarSite(site);
    }

    @Override
    public void removeStatusBarSite(StatusBarSite site) {
        statusBarView.removeStatusBarSite(site);
    }

    private void removeLastById(String messageId) {
        if (messages.size() == 0) {
            return;
        }

        for (int messageIdx = messages.size() - 1; messageIdx >= 0; messageIdx--) {
          if (messages.get(messageIdx).getKey().equals(messageId)) {
              messages.remove(messageIdx);

              break;
          }
        }
    }
}