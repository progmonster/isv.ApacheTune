package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.statusbar.StatusBarSite;
import com.apachetune.core.ui.statusbar.StatusBarView;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.*;

import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class StatusBarViewImpl implements StatusBarView {
    private final JFrame mainFrame;

    private final JPanel statusBar = new JPanel();

    private final JLabel mainMessageLabel = new JLabel();
    private final JLabel cursorPositionLabel = new JLabel();

    @Inject
    public StatusBarViewImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void initialize() {
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setLayout(new GridBagLayout());

        mainMessageLabel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        statusBar.add(mainMessageLabel, gbc);

        cursorPositionLabel.setBorder(BorderFactory.createEtchedBorder());

        statusBar.add(cursorPositionLabel);

        mainFrame.add(statusBar, BorderLayout.PAGE_END);

        setMainMessage("");
        setCursorPositionState(null);

        statusBar.invalidate();
    }

    public void setMainMessage(String message) {
        notNull(message, "Argument message cannot be a null"); //NON-NLS

        mainMessageLabel.setText(message);
    }

    public void setCursorPositionState(Point position) {
        if (position != null) {
            cursorPositionLabel.setText("" + position.y + ':' + position.x);
            cursorPositionLabel.setVisible(true);
        } else {
            cursorPositionLabel.setVisible(false);
            cursorPositionLabel.setText("");
        }
    }

    @Override
    public void addStatusBarSite(StatusBarSite site) {
        statusBar.add(site.getSiteComponent());
    }

    @Override
    public void removeStatusBarSite(StatusBarSite site) {
        statusBar.remove(site.getSiteComponent());
    }
}
