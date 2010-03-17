package com.apachetune.core;

import java.beans.PropertyChangeListener;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface WorkItem {
    void setId(String id);

    void initialize();

    void dispose();

    String getId();

    void setRootWorkItem(RootWorkItem rootWorkItem);

    RootWorkItem getRootWorkItem();

    void setParent(WorkItem parent);

    WorkItem getParent();

    /**
     * Test if testAncestor is an ancestor of this work item.
     *
     * @param testAncestor Work item wich will be tested if it an ancestor of this work item.
     * @return <code>true</code> - if testAncestor is an ancestor of this work item, <code>false</code> - otherwise. 
     */
    boolean hasAncestor(WorkItem testAncestor);

    /**
     * FIXDOC
     *
     * Before adding children to this work item, it should be added to root work item (even if indirectly). 
     *
     * @param workItem FIXDOC
     */
    void addChildWorkItem(WorkItem workItem);

    boolean hasDirectChildWorkItem(String workItemId);

    boolean hasChildWorkItem(String workItemId);

    WorkItem getDirectChildWorkItem(String workItemId);

    WorkItem getChildWorkItem(String workItemId);

    void removeDirectChildWorkItem(WorkItem workItem);

    void removeDirectChildWorkItem(String workItemId);

    void raiseEvent(String eventId, Object data, WorkItem caller);

    void raiseEvent(String eventId, Object data);

    void raiseEvent(String eventId);

    void setState(String name, Object state);

    boolean hasState(String name);

    Object getState(String name);

    Object removeState(String name);

    /**
     * Activates this work item and all its parents by a sequence.
     *
     * In the same time can be activated only one branch of workItem (from root work item to this work item). If this
     * work item already was activated, the method call will be ignored. Activation of work item parent will be stopped
     * on first active parent workItem (if exists). Before activation branch in wich this work item belongs, currently
     * active branch (if it different with the branch going to activated) will be deactivated (before a common active
     * parent work item exclusively).
     */
    void activate();

    /**
     * Deactivates this work item and all its children.
     *
     * If this work is inactive, the method call will be ignored and no events will be raised.
     */
    void deactivate();

    boolean isActive();

    /**
     * Returns a most deep active child in work item hierarchy (may be itself) of this work item.
     *
     * If this work item is not activated, method will be return <code>null</code>.
     *
     * @return A top active child (or itself if this work item has no children) or <code>null</code> if this work item
     * is not activated.
     */
    WorkItem getActiveChild();

    /**
     * Returns a direct active child of this work item if one presents.
     *
     * If this work item is not activated, method will be return <code>null</code>.
     *
     * @return A direct active child or <code>null</code> if this work item has no active children.
     */
    WorkItem getDirectActiveChild();

    void addActivationListener(ActivationListener listener);

    void removeActivationListener(ActivationListener listener);

    void removeAllActivationListeners();

    void addLifecycleListener(WorkItemLifecycleListener listener); // TODO write tests

    void removeLifecycleListener(WorkItemLifecycleListener listener); // TODO write tests

    void removeAllLifecycleListeners(); // TODO write tests

    void addPropertyChangeListener(PropertyChangeListener listener); // TODO write tests

    void removePropertyChangeListener(PropertyChangeListener listener); // TODO write tests

    void removeAllPropertyChangeListeners(); // TODO write tests
}
