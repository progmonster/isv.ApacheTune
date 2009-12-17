package com.apachetune.core.impl;

import com.apachetune.core.*;
import static com.apachetune.core.Constants.*;

import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class RootWorkItemImpl extends GenericWorkItem implements RootWorkItem {
    private final List<ActivationListener> childActivationListeners = new ArrayList<ActivationListener>();

    public RootWorkItemImpl() {
        super(ROOT_WORK_ITEM_ID);

        setRootWorkItem(this);
    }

    protected void doInitialize() {
        // No-op.
    }

    protected void doDispose() {
        // No-op.
    }

    public void addChildActivationListener(ActivationListener childActivationListener) {
        if (childActivationListener == null) {
            throw new NullPointerException("Argument activationListener cannot be a null [this = " + this + "]");
        }

        childActivationListeners.add(childActivationListener);
    }

    public void removeChildActivationListener(ActivationListener childActivationListener) {
        if (childActivationListener == null) {
            throw new NullPointerException("Argument activationListener cannot be a null [this = " + this + "]");
        }

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
}
