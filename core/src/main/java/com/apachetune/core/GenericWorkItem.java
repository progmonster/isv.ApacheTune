package com.apachetune.core;

import com.apachetune.core.impl.RootWorkItemImpl;
import com.apachetune.core.utils.Utils;
import org.apache.commons.collections.Predicate;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import static com.apachetune.core.Constants.EMPTY_EVENT;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
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
        notNull(id, "Argument id cannot be a null [this = " + this + "]");
        notEmpty(id, "Argument id cannot be empty [this = " + this + "]");

        this.id = id;
    }

    public final void initialize() {
        if (isInitialized) {
            return;
        }

        isTrue(!wasInitialized, "Work item cannot be initialized repeatedly [this = " + this + ']');

        notNull(rootWorkItem, "It should setting up a root work item before initializing.");

        isTrue((parent != null) || rootWorkItem.equals(this), "It should setting up a parent work item before initializing.");

        doInitialize();

        isInitialized = true;
        wasInitialized = true;

        fireOnInitializedEvent();

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
            getParent().removeDirectChildWorkItem(this);
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

    public final WorkItem getDirectChildWorkItem(String workItemId) {
        notNull(workItemId, "Argument workItemId cannot be a null [this = " + this + "]");

        isTrue(!workItemId.isEmpty(), "Argument workItemId cannot be empty [this = " + this + "]");

        isTrue(childWorkItems.containsKey(workItemId),
                "Work item is not contained in a children list [workItemId = \"" + workItemId + "\"; this = " + this +
                        "]");

        return childWorkItems.get(workItemId);
    }

    public WorkItem getChildWorkItem(final String workItemId) {
        notNull(workItemId, "Argument workItemId cannot be a null [this = " + this + "]");

        isTrue(!workItemId.isEmpty(), "Argument workItemId cannot be empty [this = " + this + "]");

        if (hasDirectChildWorkItem(workItemId)) {
            return getDirectChildWorkItem(workItemId);
        }

        for (WorkItem childWorkItem : childWorkItems.values()) {
            if (childWorkItem.hasChildWorkItem(workItemId)) {
                return childWorkItem.getChildWorkItem(workItemId);
            }
        }

        return null;
    }

    public final void addChildWorkItem(WorkItem workItem) {
        notNull(workItem, "Argument workItem cannot be a null [this = " + this + "]");

        isTrue(!workItem.equals(this), "Cannot add work item to himself [workItem = " + workItem + "]");

        notNull(workItem.getParent(), "Work item already has a parent [workItem = " + workItem + "; this = " +
                    this + "]");

        notNull(workItem.getId(), "Work item to add should has a not null id [workItem = " + workItem +
                    "; this = " + this + "]");

        isTrue(!childWorkItems.containsValue(workItem),
                "Work item already was added [workItem = " + workItem + "; this = " + this + "]");

        notNull(rootWorkItem, "It should set up a parent and a root work items to the container" +
                    " before adding a child workItem to him [workItem = " + workItem + "; this = " + this + "]");

        childWorkItems.put(workItem.getId(), workItem);

        workItem.setParent(this);
        workItem.setRootWorkItem(rootWorkItem);
    }

    public boolean hasDirectChildWorkItem(final String workItemId) {
        notNull(workItemId, "Argument workItemId cannot be a null [this = " + this + "]");

        isTrue(!workItemId.isEmpty(), "Argument workItemId cannot be empty [this = " + this + "]");

        return childWorkItems.containsKey(workItemId);
    }

    public boolean hasChildWorkItem(final String workItemId) {
        notNull(workItemId, "Argument workItemId cannot be a null [this = " + this + "]");

        isTrue(!workItemId.isEmpty(), "Argument workItemId cannot be empty [this = " + this + "]");

        return hasDirectChildWorkItem(workItemId) || (find(childWorkItems.values(), new Predicate() {
            public boolean evaluate(final Object object) {
                WorkItem childWorkItem = (WorkItem) object;
                
                return childWorkItem.hasChildWorkItem(workItemId);
            }
        }) != null);
    }

    public final void removeDirectChildWorkItem(String workItemId) {
        notNull(workItemId, "Argument workItemId cannot be a null [this = " + this + "]");

        isTrue(!workItemId.isEmpty(), "Argument workItemId cannot be empty [this = " + this + "]");

        isTrue(childWorkItems.containsKey(workItemId), "Work item is not contains in a children list [workItemId = " +
                    workItemId + "; this = " + this + "]");

        WorkItem workItemToRemove = childWorkItems.get(workItemId);

        workItemToRemove.deactivate();

        childWorkItems.remove(workItemId);

        workItemToRemove.setParent(null);
        workItemToRemove.setRootWorkItem(null);
    }

    public final void removeDirectChildWorkItem(WorkItem workItem) {
        notNull(workItem, "Argument workItem cannot be a null [this = " + this + "]");

        removeDirectChildWorkItem(workItem.getId());
    }

    public final void raiseEvent(String eventId, Object data, WorkItem caller) {
        notNull(eventId, "Argument eventId cannot be a null [this = " + this + "]");

        isTrue(!eventId.isEmpty(), "Argument eventId cannot be empty [this = " + this + "]");

        if (eventId.equals(EMPTY_EVENT)) {
            return;
        }

        isTrue((parent != null) || rootWorkItem.equals(this),
                "Work item must have a parent or should be a root work item before raising an event [this = " + this +
                        "]");

        if (caller == null) {
            caller = this;
        }

        callObjectHandlers(this, eventId, data);

        onRaiseEvent(eventId, data, caller);

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
        notNull(name, "Argument name cannot be a null [this = " + this + "]");

        isTrue(!name.isEmpty(), "Argument name cannot be empty [this = " + this + "]");

        if ((getParent() != null) && getParent().hasState(name)) {
            getParent().setState(name, state);
        } else {
            states.put(name, state);
        }
    }

    public final boolean hasState(String name) {
        notNull(name, "Argument name cannot be a null [this = " + this + "]");

        isTrue(!name.isEmpty(), "Argument name cannot be empty [this = " + this + "]");

        return states.containsKey(name) || ((getParent() != null) && getParent().hasState(name));
    }

    public final Object getState(String name) {
        notNull(name, "Argument name cannot be a null [this = " + this + "]");

        isTrue(!name.isEmpty(), "Argument name cannot be empty [this = " + this + "]");

        Object state;

        if (states.containsKey(name)) {
            state = states.get(name);
        } else if ((getParent() != null) && getParent().hasState(name)) {
            state = getParent().getState(name);
        } else {
            throw new IllegalArgumentException("Work item or its parent not contains specified state [name = \"" +
                    name + "\";" + " this = " + this + "]");
        }

        return state;
    }

    public final Object removeState(String name) {
        notNull(name, "Argument name cannot be a null [this = " + this + "]");

        isTrue(!name.isEmpty(), "Argument name cannot be empty [this = " + this + "]");

        isTrue(hasState(name), "Required state not found in workItem or its parents [name = \"" + name +
                    "\"; this = " + this + "]");

        if (states.containsKey(name)) {
            return states.remove(name);
        } else {
            return parent.removeState(name);
        }
    }

    public boolean hasAncestor(WorkItem testAncestor) {
        notNull(testAncestor, "Argument testAncestor cannot be a null [this = " + this + "]");

        isTrue((parent != null) || rootWorkItem.equals(this),
                "Work item must have a parent or should be a root work item before call this method [this = " + this +
                        "]");

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

    public WorkItem getDirectActiveChild() {
        if (!isActive()) {
            return null;
        }

        for (WorkItem childWorkItem : childWorkItems.values()) {
            if (childWorkItem.isActive()) {
                return childWorkItem;
            }
        }

        return null;
    }

    public void addActivationListener(ActivationListener listener) {
        notNull(listener, "Argument listener cannot be a null [this = " + this + "]");

        activationListeners.add(listener);
    }

    public void removeActivationListener(ActivationListener listener) {
        notNull(listener, "Argument listener cannot be a null [this = " + this + "]");

        activationListeners.remove(listener);
    }

    public void removeAllActivationListeners() {
        activationListeners.clear();
    }

    public void addLifecycleListener(WorkItemLifecycleListener listener) {
        notNull(listener, "Argument listener cannot be a null [this = " + this + "]");

        lifecycleListeners.add(listener);
    }

    public void removeAllLifecycleListeners() {
        lifecycleListeners.clear();
    }

    public void removeLifecycleListener(WorkItemLifecycleListener listener) {
        notNull(listener, "Argument listener cannot be a null [this = " + this + "]");

        lifecycleListeners.remove(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        notNull(propertyChangeSupport, "Argument propertyChangeSupport cannot be a null [this = " + this + "]");

        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeAllPropertyChangeListeners() {
        List<PropertyChangeListener> listeners = asList(propertyChangeSupport.getPropertyChangeListeners());

        for (PropertyChangeListener listener : listeners) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        notNull(propertyChangeSupport, "Argument propertyChangeSupport cannot be a null [this = " + this + "]");

        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "WorkItem [id: " + id + ']';
    }

    protected void onRaiseEvent(String eventId, Object data, WorkItem caller) {
        // No-op.
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

    protected final void callObjectHandlers(Object obj, String eventId, Object data) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            Subscriber subscriberAnn =  method.getAnnotation(Subscriber.class);

            if (subscriberAnn != null) {
                String annEventId = subscriberAnn.eventId();

                if (eventId.equals(annEventId)) {
                    try {
                        method.setAccessible(true);

                        Class<?>[] paramTypes = method.getParameterTypes();

                        if (paramTypes.length == 0) {
                            method.invoke(obj);
                        } else {
                            method.invoke(obj, data);
                        }
                    } catch (IllegalAccessException e) {
                        throw createRuntimeException(format("Error occurred during handling work item event [eventId={0}; obj={1}; data={2}]", eventId, obj, data), e);
                    } catch (InvocationTargetException e) {
                        throw createRuntimeException(format("Error occurred during handling work item event [eventId={0}; obj={1}; data={2}]", eventId, obj, data), e);
                    }
                }
            }
        }
    }

    private GenericWorkItem getCommonAncestor(GenericWorkItem workItem) {
        isTrue(!equals(workItem), "Argument cannot be this work item [this = " + this + "]");

        isTrue(!hasAncestor(workItem), "Argument cannot be an ancestor of this work item [workItem = " +
                    workItem + "; this = " + this + "]");

        isTrue(!workItem.hasAncestor(this), "Argument cannot be an descendant of this work item [workItem = " +
                    workItem + "; this = " + this + "]");

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

    private void checkParentWorkItemExists() {
        checkRootWorkItemExists();

        isTrue((parent != null) || rootWorkItem.equals(this),
                "Work item must have a parent or should be a root work item [this = " + this + "]");
    }

    private void checkRootWorkItemExists() {
        notNull(rootWorkItem, "Work item must have a root work item [this = " + this + "]");
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
