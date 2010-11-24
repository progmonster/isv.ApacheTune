package com.apachetune.httpserver.ui.updating;

import com.apachetune.core.ApplicationException;

/**
 * FIXDOC
 */
public class UpdateException extends ApplicationException {
    public UpdateException(String msg) {
        super(msg);
    }

    public UpdateException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
