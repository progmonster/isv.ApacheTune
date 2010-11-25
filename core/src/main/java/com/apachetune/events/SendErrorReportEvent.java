package com.apachetune.events;

import java.awt.*;

/**
 * FIXDOC
 */
public class SendErrorReportEvent {
    private final Component parentComponent;

    private final Throwable cause;

    public SendErrorReportEvent(Component parentComponent, Throwable cause) {
        this.parentComponent = parentComponent;
        this.cause = cause;
    }

    public final Component getParentComponent() {
        return parentComponent;
    }

    public final Throwable getCause() {
        return cause;
    }
}
