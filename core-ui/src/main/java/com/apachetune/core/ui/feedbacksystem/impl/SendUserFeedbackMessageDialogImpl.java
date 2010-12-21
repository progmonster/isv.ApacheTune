package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ui.feedbacksystem.SendUserFeedbackMessageDialog;
import com.google.inject.Inject;

import javax.swing.*;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;

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
    public final int showError(Throwable cause) {
        return showConfirmDialog(mainFrame, format( // TODO localize
                "Oops! An error occurred during sending the feedback.\n It''s may be temporary Internet connection" +
                " error, but it may be also internal application error.\n\n" +
                "Thanks for feedback and don''t worry: it was" +
                " saved into the application log.\nPlease, press YES to send information about the error and the" +
                " application log to our developer command.\n\n" + "Error details:\n[errorMsg={0}]\n",
                cause != null ? "" + cause.getMessage() : ""),
                "Error", // todo localize
                ERROR_MESSAGE, OK_CANCEL_OPTION);
    }

    @Override
    public final void showSuccess() {
        showMessageDialog(
                mainFrame,
                "Thanks for sending your feedback!", // todo localize
                "Success", // todo localize
                INFORMATION_MESSAGE);
    }
}
