package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.StatusBarManager;
import com.apachetune.core.ui.StatusBarView;
import com.google.inject.Inject;

import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
        if (messageId == null) {
            throw new NullPointerException("Argument messageId cannot be a null [this = " + this + "]");
        }

        if (messageId.isEmpty()) {
            throw new IllegalArgumentException("Argument messageId cannot be empty [this = " + this + "]");
        }

        if (status == null) {
            throw new NullPointerException("Argument status cannot be a null [this = " + this + "]");
        }

        if (status.isEmpty()) {
            throw new IllegalArgumentException("Argument status cannot be empty [this = " + this + "]");
        }

        messages.add(new AbstractMap.SimpleImmutableEntry<String, String>(messageId, status));

        statusBarView.setMainMessage(status);
    }

    public void removeMainStatus(String messageId) {
        if (messageId == null) {
            throw new NullPointerException("Argument messageId cannot be a null [this = " + this + "]");
        }

        if (messageId.isEmpty()) {
            throw new IllegalArgumentException("Argument messageId cannot be empty [this = " + this + "]");
        }

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