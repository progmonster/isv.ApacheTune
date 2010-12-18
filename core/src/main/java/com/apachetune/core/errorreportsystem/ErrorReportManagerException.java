package com.apachetune.core.errorreportsystem;

import com.apachetune.core.ApplicationException;

/**
 * FIXDOC
 */
public class ErrorReportManagerException extends ApplicationException {
    public ErrorReportManagerException(String msg) {
        super(msg);
    }

    public ErrorReportManagerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ErrorReportManagerException(Throwable cause) {
        super(cause);
    }
}
