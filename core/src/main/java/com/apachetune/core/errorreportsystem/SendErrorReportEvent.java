package com.apachetune.core.errorreportsystem;

import java.awt.*;

/**
 * FIXDOC
 */
public class SendErrorReportEvent {
    private final Component parentComponent;

    private final String errorMessage;

    private final Throwable cause;

    public SendErrorReportEvent(Component parentComponent, String errorMessage, Throwable cause) {
        this.parentComponent = parentComponent;
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    public final Component getParentComponent() {
        return parentComponent;
    }

    public final String getErrorMessage() {
        return errorMessage;
    }

    public final Throwable getCause() {
        return cause;
    }
}
