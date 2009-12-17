package com.apachetune.core;

import static com.apachetune.core.Constants.*;
import com.apachetune.core.impl.*;

import java.beans.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public abstract class GenericWorkItem implements WorkItem {
    private final Map<String, WorkItem> childWorkItems = new LinkedHashMap<String, WorkItem>();

    private final Map<String, Object> states = new HashMap<String, Object>();

    private final List<ActivationListener> activationListeners = new ArrayList<ActivationListener>();

    private final List<WorkItemLifecycleListener> lifecycleListeners = new ArrayList<WorkItemLifecycleListener>();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private String id;

    private RootWorkItem rootWorkItem;

    private WorkItem parent;

    private boolean isInitialized;

    private boolean wasInitialized;

    private boolean isActive;

    private boolean isAboutToBeActivated;

    private boolean isToBeDeactivated;

    protected GenericWorkItem() {
        // No-op.
    }

    /**
     * FIXDOC
     *
     * @param id FIXDOC
     */
    public GenericWorkItem(String id) {
        this.id = id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new NullPointerException("Argument id cannot be a null [this = " + this + "]");
        }

        if (id.isEmpty()) {
            throw new IllegalArgumentException("Argument id cannot be empty [this = " + this + "]");
        }

        this.id = id;
    }

    public final void initialize() {
        if (isInitialized) {
            return;
        }

        if (wasInitialized) {
            throw new IllegalStateException("Work item cannot be initialized repeatedly [this = " + this + ']');
        }

        if (rootWorkItem == null) {
            throw new NullPointerException("It should setting up a root work item before initializing.");
        }

        if ((parent == null) && !rootWorkItem.equals(this)) {
            throw new NullPointerException("It should setting up a parent work item before initializing.");
        }

        // TODO catch an exception and log it (maybe send to support, maybe deffered if no UI accesible).
        doInitialize();

        isInitialized = true;
        wasInitialized = true;

        fireOnInitializedEvent();

        // TODO catch child exceptions and log it (maybe send to support, maybe deffered if no UI accesible).

        List<WorkItem> childWorkItemListCopy = new ArrayList<WorkItem>(childWorkItems.values());

        for (WorkItem childWorkItem : childWorkItemListCopy) {
            childWorkItem.initialize();
        }
    }

    public final void dispose() {
        if (!isInitialized) {
            return;
        }
        
        // TODO catch child dispose exceptions and log it (maybe send to support, maybe deffered if no UI accesible).

        List<WorkItem> childWorkItemListCopy = new ArrayList<WorkItem>(childWorkItems.values());

        for (WorkItem childWorkItem : childWorkItemListCopy) {
            childWorkItem.dispose();
        }

        deactivate();

        // TODO catch an exception and log it (maybe send to support, maybe deffered if no UI accesible).
        doDispose();

        if (getParent() != null) {
            getParent().removeChildWorkItem(this);
        }

        isInitialized = false;

        fireOnDisposedEvent();
    }

    public final String getId() {
        return id;
    }

    public final void setRootWorkItem(RootWorkItem rootWorkItem) {
        this.rootWorkItem = rootWorkItem;
    }

    public final RootWorkItemImpl getRootWorkItem() {
        return (RootWorkItemImpl) rootWorkItem;
    }

    public final void setParent(WorkItem parent) {
        this.parent = parent;
    }

    public final GenericWorkItem getParent() {
        return (GenericWorkItem) parent;
    }

    public final WorkItem getChildWorkItem(String workItemId) {
        if (workItemId == null) {
            throw new NullPointerException("Argument workItemId cannot be a null [this = " + this + "]");
        }

        if (workItemId.isEmpty()) {
            throw new IllegalArgumentException("Argument workItemId cannot be empty [this = " + this + "]");
        }

        if (!childWorkItems.containsKey(workItemId)) {
            throw new IllegalArgumentException("Work item is not contained in a children list [workItemId = \"" +
                    workItemId + "\"; this = " + this + "]");
        }

        return childWorkItems.get(workItemId);
    }

    public final void addChildWorkItem(WorkItem workItem) {
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        if (workItem.equals(this)) {
            throw new IllegalArgumentException("Cannot add work item to himself [workItem = " + workItem + "]");
        }

        if (workItem.getParent() != null) {
            throw new IllegalArgumentException("Work item already has a parent [workItem = " + workItem + "; this = " +
                    this + "]");
        }

        if (workItem.getId() == null) {
            throw new IllegalArgumentException("Work item to add should has a not null id [workItem = " + workItem +
                    "; this = " + this + "]");
        }

        if (childWorkItems.containsValue(workItem)) {
            throw new IllegalArgumentException("Work item already was added [workItem = " + workItem + "; this = " +
                    this + "]");
        }

        if (rootWorkItem == null) {
            throw new IllegalArgumentException("It should set up a parent and a root work items to the container" +
                    " before adding a child workItem to him [workItem = " + workItem + "; this = " + this + "]");
        }

        childWorkItems.put(workItem.getId(), workItem);

        workItem.setParent(this);
        workItem.setRootWorkItem(rootWorkItem);
    }

    public final void removeChildWorkItem(String workItemId) {
        if (workItemId == null) {
            throw new NullPointerException("Argument workItemId cannot be a null [this = " + this + "]");
        }

        if (workItemId.isEmpty()) {
            throw new IllegalArgumentException("Argument workItemId cannot be empty [this = " + this + "]");
        }

        if (!childWorkItems.containsKey(workItemId)) {
            throw new IllegalArgumentException("Work item is not contains in a children list [workItemId = " +
                    workItemId + "; this = " + this + "]");
        }

        WorkItem workItemToRemove = childWorkItems.get(workItemId);

        workItemToRemove.deactivate();

        childWorkItems.remove(workItemId);

        workItemToRemove.setParent(null);
        workItemToRemove.setRootWorkItem(null);
    }

    public final void removeChildWorkItem(WorkItem workItem) {
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        removeChildWorkItem(workItem.getId());
    }

    public final void raiseEvent(String eventId, Object data, WorkItem caller) {
        if (eventId == null) {
            throw new NullPointerException("Argument eventId cannot be a null [this = " + this + "]");
        }

        if (eventId.isEmpty()) {
            throw new IllegalArgumentException("Argument eventId cannot be empty [this = " + this + "]");
        }

        if (eventId.equals(EMPTY_EVENT)) {
            return;
        }

        if ((parent == null) && !rootWorkItem.equals(this)) {
            throw new IllegalArgumentException("Work item must have a parent or should be a root work item before" +
                    " raising an event [this = " + this + "]");
        }

        if (caller == null) {
            caller = this;
        }

        callHandlers(eventId, data);

        List<WorkItem> childWorkItemListCopy = new ArrayList<WorkItem>(childWorkItems.values());

        for (WorkItem childWorkItem : childWorkItemListCopy) {
            if (!caller.equals(childWorkItem)) {
                childWorkItem.raiseEvent(eventId, data, this);
            }
        }

        if ((parent != null) && !parent.equals(caller)) {
            parent.raiseEvent(eventId, data, this);
        }
    }

    public final void raiseEvent(String eventId, Object data) {
        raiseEvent(eventId, data, null);
    }

    public final void raiseEvent(String eventId) {
        raiseEvent(eventId, null);
    }

    public final void setState(String name, Object state) {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null [this = " + this + "]");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty [this = " + this + "]");
        }

        if ((getParent() != null) && getParent().hasState(name)) {
            getParent().setState(name, state);
        } else {
            states.put(name, state);
        }
    }

    public final boolean hasState(String name) {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null [this = " + this + "]");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty [this = " + this + "]");
        }

        return states.containsKey(name) || ((getParent() != null) && getParent().hasState(name));
    }

    public final Object getState(String name) {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null [this = " + this + "]");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty [this = " + this + "]");
        }

        Object state;

        if (states.containsKey(name)) {
            state = states.get(name);
        } else if ((getParent() != null) && getParent().hasState(name)) {
            state = getParent().getState(name);
        } else {
            throw new IllegalArgumentException("Work item or its parent not contain specified state [name = \"" +
                    name + "\";" + " this = " + this + "]");
        }

        return state;
    }

    public final Object removeState(String name) {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null [this = " + this + "]");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty [this = " + this + "]");
        }

        if (!hasState(name)) {
            throw new IllegalArgumentException("Required state not found in workItem or its parents [name = \"" + name +
                    "\"; this = " + this + "]");
        }

        if (states.containsKey(name)) {
            return states.remove(name);
        } else {
            return parent.removeState(name);
        }
    }

    public boolean hasAncestor(WorkItem testAncestor) {
        if (testAncestor == null) {
            throw new NullPointerException("Argument testAncestor cannot be a null [this = " + this + "]");
        }

        if ((parent == null) && !rootWorkItem.equals(this)) {
            throw new IllegalArgumentException("Work item must have a parent or should be a root work item before" +
                    " call this method [this = " + this + "]");
        }
        
        WorkItem parent = getParent();

        return (parent != null) && ((parent.equals(testAncestor)) || parent.hasAncestor(testAncestor));  
    }

    public void activate() {
        checkRootWorkItemExists();
        checkParentWorkItemExists();

        if (isActive() || isAboutToBeActivated) {
            return;
        }

        isAboutToBeActivated = true;

        GenericWorkItem activeWorkItem = (GenericWorkItem) getRootWorkItem().getActiveChild();

        if (activeWorkItem != null) {
            if (!hasAncestor(activeWorkItem)) {
                GenericWorkItem commonAncestor = getCommonAncestor(activeWorkItem);

                commonAncestor.deactivateChildren();
            }
        }

        WorkItem parentWorkItem = getParent();

        if (parentWorkItem != null) {
            parentWorkItem.activate();
        }
        
        doActivation();

        isActive = true;
        isAboutToBeActivated = false;
        
        fireOnActivateEvent();

        if (!isRootWorkItem()) {
            getRootWorkItem().fireOnChildActivateEvent(this);
        }
    }

    public void deactivate() {
        checkRootWorkItemExists();
        checkParentWorkItemExists();

        if (!isActive || isToBeDeactivated) {
            return;
        }

        isToBeDeactivated = true;

        deactivateChildren();

        doDeactivation();

        isActive = false;
        isToBeDeactivated = false;

        fireOnDeactivateEvent();

        if (!isRootWorkItem()) {
            getRootWorkItem().fireOnChildDeactivateEvent(this);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public WorkItem getActiveChild() {
        if (!isActive()) {
            return null;
        }

        for (WorkItem childWorkItem : childWorkItems.values()) {
            if (childWorkItem.isActive()) {
                return childWorkItem.getActiveChild();
            }
        }

        return this;
    }

    public void addActivationListener(ActivationListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        activationListeners.add(listener);
    }

    public void removeActivationListener(ActivationListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        activationListeners.remove(listener);
    }

    public void removeAllActivationListeners() {
        activationListeners.clear();
    }

    public void addLifecycleListener(WorkItemLifecycleListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }

        lifecycleListeners.add(listener);
    }

    public void removeAllLifecycleListeners() {
        lifecycleListeners.clear();
    }

    public void removeLifecycleListener(WorkItemLifecycleListener listener) {
        if (listener == null) {
            throw new NullPointerException("Argument listener cannot be a null [this = " + this + "]");
        }
        
        lifecycleListeners.remove(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport == null) {
            throw new NullPointerException("Argument propertyChangeSupport cannot be a null [this = " + this + "]");
        }

        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeAllPropertyChangeListeners() {
        List<PropertyChangeListener> listeners = asList(propertyChangeSupport.getPropertyChangeListeners());

        for (PropertyChangeListener listener : listeners) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport == null) {
            throw new NullPointerException("Argument propertyChangeSupport cannot be a null [this = " + this + "]");
        }

        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "WorkItem [id: " + id + ']';
    }

    protected abstract void doInitialize();

    protected abstract void doDispose();

    protected void doActivation() {
        // No-op.
    }

    protected void doDeactivation() {
        // No-op.
    }

    protected final void deactivateChildren() {
        List<WorkItem> childWorkItemListCopy = new ArrayList<WorkItem>(childWorkItems.values());

        for (WorkItem childWorkItem : childWorkItemListCopy) {
            if (childWorkItem.isActive()) {
                childWorkItem.deactivate();

                break;
            }
        }
    }

    protected final void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);        
    }

    private GenericWorkItem getCommonAncestor(GenericWorkItem workItem) {
        if (equals(workItem)) {
            throw new IllegalArgumentException("Argument cannot be this work item [this = " + this + "]");
        }

        if (hasAncestor(workItem)) {
            throw new IllegalArgumentException("Argument cannot be an ancestor of this work item [workItem = " +
                    workItem + "; this = " + this + "]");
        }

        if (workItem.hasAncestor(this)) {
            throw new IllegalArgumentException("Argument cannot be an descendant of this work item [workItem = " +
                    workItem + "; this = " + this + "]");
        }

        GenericWorkItem thisParent = getParent();

        if (workItem.hasAncestor(thisParent)) {
            return thisParent;
        }

        GenericWorkItem workItemParent = workItem.getParent();

        if (hasAncestor(workItemParent)) {
            return workItemParent;
        }

        return thisParent.getCommonAncestor(workItemParent);
    }

    private void fireOnActivateEvent() {
        List<ActivationListener> listeners = new ArrayList<ActivationListener>(activationListeners);

        for (ActivationListener listener : listeners) {
            listener.onActivate(this);
        }
    }

    private void fireOnDeactivateEvent() {
        List<ActivationListener> listeners = new ArrayList<ActivationListener>(activationListeners);

        for (ActivationListener listener : listeners) {
            listener.onDeactivate(this);
        }
    }

    private void callHandlers(String eventId, Object data) {
        for (Method method : getClass().getDeclaredMethods()) {
            Subscriber subscriberAnn =  method.getAnnotation(Subscriber.class);

            if (subscriberAnn != null) {
                String annEventId = subscriberAnn.eventId();

                if (eventId.equals(annEventId)) {
                    try {
                        method.setAccessible(true);

                        Class<?>[] paramTypes = method.getParameterTypes();

                        if (paramTypes.length == 0) {
                            method.invoke(this);
                        } else {
                            method.invoke(this, data);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void checkParentWorkItemExists() {
        checkRootWorkItemExists();

        if ((parent == null) && !rootWorkItem.equals(this)) {
            throw new IllegalStateException("Work item must have a parent or should be a root work item [this = " +
                    this + "]");
        }
    }

    private void checkRootWorkItemExists() {
        if (rootWorkItem == null) {
            throw new IllegalStateException("Work item must have a root work item [this = " + this + "]");
        }
    }

    private boolean isRootWorkItem() {
        return (this instanceof RootWorkItem);
    }

    private void fireOnDisposedEvent() {
        List<WorkItemLifecycleListener> listenerListCopy = new ArrayList<WorkItemLifecycleListener>(lifecycleListeners);

        for (WorkItemLifecycleListener listener : listenerListCopy) {
            listener.onDisposed(this);
        }
    }

    private void fireOnInitializedEvent() {
        List<WorkItemLifecycleListener> listenerListCopy = new ArrayList<WorkItemLifecycleListener>(lifecycleListeners);

        for (WorkItemLifecycleListener listener : listenerListCopy) {
            listener.onInitialized(this);
        }
    }
}
