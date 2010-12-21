package com.apachetune.core.errorreportsystem;

import java.awt.*;

/**
 * FIXDOC
 */
public class SendErrorReportEvent {
    private final Component parentComponent;

    private final String errorMessage;

    private final Throwable cause;

    public final boolean showSendCancelDialog;

    public SendErrorReportEvent(Component parentComponent, String errorMessage, Throwable cause,
                                boolean showSendCancelDialog) {
        this.parentComponent = parentComponent;
        this.errorMessage = errorMessage;
        this.cause = cause;
        this.showSendCancelDialog = showSendCancelDialog;
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

    public boolean isShowSendCancelDialog() {
        return showSendCancelDialog;
    }
}
