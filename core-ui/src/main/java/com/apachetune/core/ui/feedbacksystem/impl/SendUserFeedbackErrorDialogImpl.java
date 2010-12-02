package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ui.feedbacksystem.SendUserFeedbackErrorDialog;
import com.google.inject.Inject;

import javax.swing.*;
import java.text.MessageFormat;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * FIXDOC
 */
public class SendUserFeedbackErrorDialogImpl implements SendUserFeedbackErrorDialog {
    private final JFrame mainFrame;

    @Inject
    public SendUserFeedbackErrorDialogImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public final void show(Throwable cause) {
        // TODO implement
        showMessageDialog(mainFrame, MessageFormat.format("Error sending user feedback [{0}]", cause.getMessage()),
                "Error", ERROR_MESSAGE);
    }
}
