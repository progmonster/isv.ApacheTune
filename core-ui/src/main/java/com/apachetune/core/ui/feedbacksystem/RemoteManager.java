package com.apachetune.core.ui.feedbacksystem;

/**
 * FIXDOC
 */
public interface RemoteManager {
    void sendUserFeedback(String userEMail, String userMessage) throws RemoteException;
}
