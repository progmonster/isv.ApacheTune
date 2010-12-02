package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ui.feedbacksystem.SendUserFeedbackMessageDialog;
import com.google.inject.Inject;

import javax.swing.*;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class SendUserFeedbackMessageDialogImpl implements SendUserFeedbackMessageDialog {
    private final JFrame mainFrame;

    @Inject
    public SendUserFeedbackMessageDialogImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public final void showError(Throwable cause) {
        // TODO implement
        showMessageDialog(mainFrame, format("Error sending user feedback [{0}]", cause.getMessage()),
                "Error", ERROR_MESSAGE);
    }

    @Override
    public final void showSuccess() {
        showMessageDialog(mainFrame, "Thanks for sending your feedback!", "Success", INFORMATION_MESSAGE);
    }
}
