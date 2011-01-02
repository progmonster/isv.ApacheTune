package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.ui.feedbacksystem.SendUserFeedbackMessageDialog;
import com.google.inject.Inject;

import javax.swing.*;
import java.util.ResourceBundle;

import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.*;

/**
 * FIXDOC
 */
public class SendUserFeedbackMessageDialogImpl implements SendUserFeedbackMessageDialog {
    private final JFrame mainFrame;

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(SendUserFeedbackMessageDialogImpl.class);

    @Inject
    public SendUserFeedbackMessageDialogImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public final int showError(Throwable cause) {
        return showConfirmDialog(mainFrame, format(resourceBundle.getString(
                        "sendUserFeedbackMessageDialogImpl.showError.message"), cause != null ? ""
                            + cause.getMessage() : ""),
                resourceBundle.getString("sendUserFeedbackMessageDialogImpl.showError.title"),
                ERROR_MESSAGE, OK_CANCEL_OPTION);
    }

    @Override
    public final void showSuccess() {
        showMessageDialog(
                mainFrame,
                resourceBundle.getString("sendUserFeedbackMessageDialogImpl.showSuccess.message"),
                resourceBundle.getString("sendUserFeedbackMessageDialogImpl.showSuccess.title"),
                INFORMATION_MESSAGE);
    }
}
