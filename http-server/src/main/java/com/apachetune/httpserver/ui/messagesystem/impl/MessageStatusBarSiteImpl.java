package com.apachetune.httpserver.ui.messagesystem.impl;

import com.apachetune.httpserver.ui.messagesystem.MessageStatusBarSite;
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

    private final JPanel panel;

    private final JButton button;

    public MessageStatusBarSiteImpl() {
        panel = new JPanel();

        button = new JButton("M"); // todo replace with images

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
          /*      messageSmartPartProvider.get().initialize(httpServerWorkItem);
*/
                // TODO show message list dialog
            }
        });

        panel.add(button);
    }

    @Override
    public final JComponent getSiteComponent() {
        return panel;
    }

    @Override
    public final void setNotificationAreaActive(boolean isActive) {
        button.setEnabled(isActive);
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
                // todo show message list dialog
            }
        });

        BalloonTip balloonTip =
                new BalloonTip(button, tipLabel, edgedLook, RIGHT_BELOW,
                        ALIGNED, 40, 20, true);

        TimingUtils.showTimedBalloon(balloonTip, BALLOON_TIP_SHOW_TIME);
    }
}
