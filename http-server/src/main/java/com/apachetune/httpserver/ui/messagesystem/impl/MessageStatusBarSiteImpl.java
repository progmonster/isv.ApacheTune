package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.messagesystem.MessageStatusBarSite;
import com.apachetune.httpserver.ui.messagesystem.messagedialog.MessageSmartPart;
import com.google.inject.Inject;
import com.google.inject.Provider;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Color.BLUE;
import static java.awt.Color.WHITE;
import static net.java.balloontip.BalloonTip.AttachLocation.ALIGNED;
import static net.java.balloontip.BalloonTip.Orientation.RIGHT_BELOW;

/**
 * FIXDOC
 */
public class MessageStatusBarSiteImpl implements MessageStatusBarSite {
    public static final int BALLOON_TIP_SHOW_TIME = 10000;

    private final Provider<MessageSmartPart> messageSmartPartProvider;

    private final HttpServerWorkItem httpServerWorkItem;

    private JPanel panel;

    private JButton button;

    @Inject
    public MessageStatusBarSiteImpl(Provider<MessageSmartPart> messageSmartPartProvider,
                                    HttpServerWorkItem httpServerWorkItem) {
        this.messageSmartPartProvider = messageSmartPartProvider;
        this.httpServerWorkItem = httpServerWorkItem;
    }

    @Override
    public final void initialize() {
        panel = new JPanel();

        button = new JButton("M"); // todo replace with images

        button.setEnabled(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog();
            }
        });

        panel.add(button);
    }

    @Override
    public final void dispose() {
        // No-op.
    }

    @Override
    public final JComponent getSiteComponent() {
        return panel;
    }

    @Override
    public final void setNotificationAreaActive(boolean isActive) {
        // todo show highlighted icon if active and vice versa
    }

    @Override
    public final void setNotificationTip(String tip) {
        button.setToolTipText(tip);
    }

    @Override
    public final void showBalloonTip(String tip) {
        BalloonTipStyle edgedLook = new EdgedBalloonStyle(WHITE, BLUE);

        JLabel tipLabel = new JLabel(tip);

        tipLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMessageDialog();
            }
        });

        BalloonTip balloonTip =
                new BalloonTip(button, tipLabel, edgedLook, RIGHT_BELOW,
                        ALIGNED, 40, 20, true);

        TimingUtils.showTimedBalloon(balloonTip, BALLOON_TIP_SHOW_TIME);
    }

    private void showMessageDialog() {
        MessageSmartPart msgDialogSmartPart = this.messageSmartPartProvider.get();

        msgDialogSmartPart.initialize(this.httpServerWorkItem);

        msgDialogSmartPart.run();

        msgDialogSmartPart.close();
    }
}
