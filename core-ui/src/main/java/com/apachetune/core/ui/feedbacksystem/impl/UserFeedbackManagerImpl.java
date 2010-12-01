package com.apachetune.core.ui.feedbacksystem.impl;

import com.apachetune.core.ui.UIWorkItem;
import com.apachetune.core.ui.feedbacksystem.RemoteManager;
import com.apachetune.core.ui.feedbacksystem.UserFeedbackManager;
import com.apachetune.core.ui.feedbacksystem.UserFeedbackView;
import com.apachetune.feedbacksystem.FeedbackManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.core.ui.feedbacksystem.UserFeedbackView.Result.USER_ACCEPTED_SENDING;

/**
 * FIXDOC
 */
public class UserFeedbackManagerImpl implements UserFeedbackManager {
    private final UIWorkItem workItem;

    private final Provider<UserFeedbackView> userFeedbackViewProvider;

    private final RemoteManager remoteManager;

    private final FeedbackManager feedbackManager;

    @Inject
    public UserFeedbackManagerImpl(@Named(CORE_UI_WORK_ITEM) UIWorkItem workItem,
                                   Provider<UserFeedbackView> userFeedbackViewProvider, RemoteManager remoteManager,
                                   FeedbackManager feedbackManager) {
        this.workItem = workItem;
        this.userFeedbackViewProvider = userFeedbackViewProvider;
        this.remoteManager = remoteManager;
        this.feedbackManager = feedbackManager;
    }

    @Override
    public final void sendUserFeedback() {
        UserFeedbackView userFeedbackView = userFeedbackViewProvider.get();

        userFeedbackView.initialize(workItem);

        String userEmail = feedbackManager.getUserEmail();

        userFeedbackView.setUserEmail(userEmail);

        userFeedbackView.run();

        if (userFeedbackView.getResult() != USER_ACCEPTED_SENDING) {
            return;
        }

        userEmail = userFeedbackView.getUserEmail();

        feedbackManager.storeUserEMail(userEmail);

        String userMessage = userFeedbackView.getUserMessage();

        userFeedbackView.dispose();

        remoteManager.sendUserFeedback(userEmail, userMessage);
    }
}
