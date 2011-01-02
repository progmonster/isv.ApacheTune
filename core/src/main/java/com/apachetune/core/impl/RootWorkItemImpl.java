package com.apachetune.core.impl;

import com.apachetune.core.*;
import com.apachetune.core.errorreportsystem.SendErrorReportEvent;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.utils.Utils;
import com.google.inject.Inject;
import org.apache.velocity.app.Velocity;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;

import static com.apachetune.core.Constants.ON_SEND_ERROR_REPORT_EVENT;
import static com.apachetune.core.Constants.ROOT_WORK_ITEM_ID;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.core.utils.Utils.showSendErrorReportDialog;
import static org.apache.commons.lang.Validate.notNull;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class RootWorkItemImpl extends GenericWorkItem implements RootWorkItem {
    private final List<ActivationListener> childActivationListeners = new ArrayList<ActivationListener>();

    private final AppManager appManager;

    private final Scheduler scheduler;

    private final PreferencesManager preferencesManager;

    @Inject
    public RootWorkItemImpl(AppManager appManager, Scheduler scheduler, PreferencesManager preferencesManager) {
        super(ROOT_WORK_ITEM_ID);

        this.appManager = appManager;
        this.scheduler = scheduler;
        this.preferencesManager = preferencesManager;

        setRootWorkItem(this);
    }

    protected void doInitialize() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw createRuntimeException(e);
        }

        Velocity.setProperty(RUNTIME_LOG_LOGSYSTEM_CLASS, Constants.VELOCITY_LOG_CLASS);
        Velocity.setProperty(Constants.VELOCITY_LOGGER_NAME_PROP, Constants.VELOCITY_LOGGER);

        try {
            Velocity.init();
        } catch (Exception e) {
            throw createRuntimeException(e);
        }
    }

    protected void doDispose() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw createRuntimeException(e);
        }
    }

    public void addChildActivationListener(ActivationListener childActivationListener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(childActivationListener, Utils.getArgumentCannotBeNullMessage("childActivationListener")); //NON-NLS

        childActivationListeners.add(childActivationListener);
    }

    public void removeChildActivationListener(ActivationListener childActivationListener) {
        //noinspection DuplicateStringLiteralInspection
        notNull(childActivationListener, Utils.getArgumentCannotBeNullMessage("childActivationListener")); //NON-NLS

        childActivationListeners.remove(childActivationListener);
    }

    public void removeAllChildActivationListeners() {
        childActivationListeners.clear();
    }

    public void fireOnChildActivateEvent(WorkItem childWorkItem) {
        List<ActivationListener> listeners = new ArrayList<ActivationListener>(childActivationListeners);

        for (ActivationListener listener : listeners) {
            listener.onActivate(childWorkItem);
        }
    }

    public void fireOnChildDeactivateEvent(WorkItem childWorkItem) {
        List<ActivationListener> listeners = new ArrayList<ActivationListener>(childActivationListeners);

        for (ActivationListener listener : listeners) {
            listener.onDeactivate(childWorkItem);
        }
    }

    @Subscriber(eventId = ON_SEND_ERROR_REPORT_EVENT)
    private void onSendErrorReportEvent(SendErrorReportEvent event) {
        //noinspection ThrowableResultOfMethodCallIgnored
        showSendErrorReportDialog(event.getParentComponent(), event.getErrorMessage(), event.getCause(),
                appManager, preferencesManager, event.isShowSendCancelDialog());
    }
}
