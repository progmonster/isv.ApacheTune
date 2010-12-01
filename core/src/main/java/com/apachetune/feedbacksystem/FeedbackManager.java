package com.apachetune.feedbacksystem;

/**
 * FIXDOC
 */
public interface FeedbackManager {
    void sendErrorReport(String message, Throwable cause);

    void sendAppLog();

    String getUserEmail();

    void storeUserEMail(String userEMail);
}
