package com.apachetune.core.ui.feedbacksystem;

import com.apachetune.core.ui.NView;

/**
 * FIXDOC
 */
public interface UserFeedbackView extends NView {
    enum Result {USER_ACCEPTED_SENDING, USER_REJECTED_SENDING}

    Result getResult();

    String getUserMessage();

    void setUserEmail(String userEmail);

    String getUserEmail();
}
