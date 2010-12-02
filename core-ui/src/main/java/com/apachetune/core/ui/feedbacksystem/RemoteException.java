package com.apachetune.core.ui.feedbacksystem;

import com.apachetune.core.ApplicationException;

import java.io.IOException;

/**
 * FIXDOC
 */
public class RemoteException extends ApplicationException {
    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}
