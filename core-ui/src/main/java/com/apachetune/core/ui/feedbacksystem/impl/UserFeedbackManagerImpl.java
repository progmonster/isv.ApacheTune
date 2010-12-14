package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.ui.UIWorkItem;
import com.apachetune.core.ui.feedbacksystem.*;
import com.apachetune.errorreportsystem.ErrorReportManager;
import com.apachetune.errorreportsystem.SendErrorReportEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import static com.apachetune.core.Constants.ON_SEND_ERROR_REPORT_EVENT;
import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.core.ui.feedbacksystem.UserFeedbackView.Result.USER_ACCEPTED_SENDING;
import static javax.swing.JOptionPane.OK_OPTION;

/**
 * FIXDOC
 */
public class UserFeedbackManagerImpl implements UserFeedbackManager {
    private static final Logger logger = LoggerFactory.getLogger(UserFeedbackManagerImpl.class);

    private final UIWorkItem workItem;

    private final Provider<UserFeedbackView> userFeedbackViewProvider;

    private final RemoteManager remoteManager;

    private final SendUserFeedbackMessageDialog sendUserFeedbackMessageDialog;

    private final JFrame mainFrame;

    private final PreferencesManager preferencesManager;

    @Inject
    public UserFeedbackManagerImpl(@Named(CORE_UI_WORK_ITEM) UIWorkItem workItem,
                                   Provider<UserFeedbackView> userFeedbackViewProvider, RemoteManager remoteManager,
                                   SendUserFeedbackMessageDialog sendUserFeedbackMessageDialog, JFrame mainFrame,
                                   PreferencesManager preferencesManager) {
        this.workItem = workItem;
        this.userFeedbackViewProvider = userFeedbackViewProvider;
        this.remoteManager = remoteManager;
        this.sendUserFeedbackMessageDialog = sendUserFeedbackMessageDialog;
        this.mainFrame = mainFrame;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public final void sendUserFeedback() {
        UserFeedbackView userFeedbackView = userFeedbackViewProvider.get();

        userFeedbackView.initialize(workItem);

        String userEmail = ErrorReportManager.getInstance().getUserEmail(preferencesManager);

        userFeedbackView.setUserEmail(userEmail);

        userFeedbackView.run();

        if (userFeedbackView.getResult() != USER_ACCEPTED_SENDING) {
            return;
        }

        userEmail = userFeedbackView.getUserEmail();

        ErrorReportManager.getInstance().storeUserEMail(userEmail, preferencesManager);

        String userMessage = userFeedbackView.getUserMessage();

        userFeedbackView.dispose();

        try {
            remoteManager.sendUserFeedback(userEmail, userMessage);

            sendUserFeedbackMessageDialog.showSuccess();
        } catch (RemoteException e) {
            logger.error("Error during sending user feedback [userEmail=" + userEmail + "; userMessage=" + userMessage +
                    ']');

            if (sendUserFeedbackMessageDialog.showError(e) == OK_OPTION) {
                workItem.raiseEvent(ON_SEND_ERROR_REPORT_EVENT,
                        new SendErrorReportEvent(mainFrame, "User feedback sending error", e));
            }
        }
    }
}
