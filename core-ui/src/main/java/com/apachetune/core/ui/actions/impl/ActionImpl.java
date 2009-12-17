package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.Action;
import com.apachetune.core.ui.actions.*;
import org.apache.commons.lang.*;

import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.*;
import static java.util.Arrays.*;
import java.util.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ActionImpl extends AbstractAction implements Action {
    private static final long serialVersionUID = 7982518996363512528L;

    private static final String ID_KEY = "ID";

    private Class<? extends ActionSite> actionSiteClass;

    private ActionSite actionSite;

    private boolean showInContextMenu = true;

    private ActionGroup actionGroup;

    public ActionImpl(String id, Class<? extends ActionSite> actionSiteClass) {
        if (id == null) {
            throw new NullPointerException("Argument id cannot be a null [this = " + this + "]");
        }

        if (actionSiteClass == null) {
            throw new NullPointerException("Argument actionSiteClass cannot be a null [this = " + this + "]");
        }

        setId(id);

        validateActionSiteClass(actionSiteClass);

        this.actionSiteClass = actionSiteClass;

        update();
    }

    public String getId() {
        return (String) getValue(ID_KEY);
    }

    public ActionGroup getActionGroup() {
        return actionGroup;
    }

    public Class<? extends ActionSite> getActionSiteClass() {
        return actionSiteClass;
    }

    public ActionSite getActionSite() {
        return actionSite;
    }

    public void setActionSite(ActionSite actionSite) {
        if (ObjectUtils.equals(actionSite, this.actionSite)) {
            return;
        }                

        if (actionSite != null) {
            if (!actionSiteClass.isInstance(actionSite)) {
                throw new IllegalArgumentException("Action site should has an actionSiteClass type [actionSite = " +
                        actionSite + "; actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
            }

            validateActionSiteClass(actionSite.getClass());

            validateHandlerImplementor(actionSite);
            validatePermissionMethodImplementor(actionSite);
        }

        ActionSite oldActionSite = this.actionSite;

        this.actionSite = actionSite;

        update();

        firePropertyChange("actionSite", oldActionSite, actionSite);
    }

    public void update() {
        if (actionSite == null) {
            super.setEnabled(false);
        } else {
            Method permissionMethod = getDeclaredPermissionMethod();

            try {
                super.setEnabled((Boolean) permissionMethod.invoke(actionSite));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            }
        }
    }

    public String getName() {
        return (String) getValue(NAME);
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Argument name cannot be a null [this = " + this + "]");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be empty [this = " + this + "]");
        }

        putValue(NAME, name);
    }

    public ImageIcon getSmallIcon() {
        return (ImageIcon) getValue(SMALL_ICON);
    }

    public void setSmallIcon(ImageIcon smallIcon) {
        putValue(SMALL_ICON, smallIcon);
    }

    public ImageIcon getLargeIcon() {
        return (ImageIcon) getValue(LARGE_ICON_KEY);
    }

    public void setLargeIcon(ImageIcon largeIcon) {
        putValue(LARGE_ICON_KEY, largeIcon);
    }

    public String getShortDescription() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    public void setShortDescription(String shortDescription) {
        putValue(SHORT_DESCRIPTION, shortDescription);
    }

    public String getLongDescription() {
        return (String) getValue(LONG_DESCRIPTION);
    }

    public void setLongDescription(String longDescription) {
        putValue(LONG_DESCRIPTION, longDescription);
    }

    // TODO Make this function returning a char.
    public int getMnemonicKey() {
        return (Integer) getValue(MNEMONIC_KEY);
    }

    public void setMnemonicKey(char mnemonicKey) {
        putValue(MNEMONIC_KEY, (int) mnemonicKey);
    }

    public KeyStroke getAcceleratorKey() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    public void setAcceleratorKey(KeyStroke keyStroke) {
        putValue(ACCELERATOR_KEY, keyStroke);
    }

    public void setShowInContextMenu(boolean showInContextMenu) {
        this.showInContextMenu = showInContextMenu;
    }

    public boolean canShowInContextMenu() {
        return showInContextMenu;
    }

    public void actionPerformed(ActionEvent e) {
        if (!isEnabled()) {
            throw new IllegalStateException("Cannot perform action while one is disabled [e = " + e + "; this = " +
                    this + ']');
        }

        if (actionSite == null) {
            return;
        }

        Method actionHandlerMethod = getDeclaredHandlerMethod();

        try {
            actionHandlerMethod.invoke(actionSite);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Internal error", ex); // TODO Make it with a service.
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Internal error", ex); // TODO Make it with a service.
        }
    }

    @Override
    public final void setEnabled(boolean newValue) {
        throw new IllegalStateException("Cannot perform enable/disable operation directly [newValue = " + newValue +
                "; this = " + this + "]");
    }

    @Override
    public final Action clone() {
        ActionImpl clonedObject;

        try {
            clonedObject = (ActionImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Internal exception.", e); // TODO Make it as a service.
        }

        clonedObject.actionSiteClass = actionSiteClass;
        clonedObject.actionSite = actionSite;
        clonedObject.showInContextMenu = showInContextMenu;

        return clonedObject;
    }

    public void setActionGroup(ActionGroup actionGroup) {
        this.actionGroup = actionGroup;
    }

    @Override
    public String toString() {
        return "ActionImpl [id = " + getId() + ']';
    }

    private void setId(String id) {
        putValue(ID_KEY, id);
    }

    private void validateActionSiteClass(Class<? extends ActionSite> actionSiteClass) {
        List<Method> methods = asList(actionSiteClass.getDeclaredMethods());

        boolean isHandlerFound = false;

        boolean isPermitMethodFound = false;

        for (Method method : methods) {
            ActionHandler actionHandlerAnnt = method.getAnnotation(ActionHandler.class);

            if ((actionHandlerAnnt != null) && actionHandlerAnnt.value().equals(getId())) {
                if (isHandlerFound) {
                    throw new RuntimeException("Action site class can contains only one handler per action [" +
                            "actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
                }

                isHandlerFound = true;

                checkActionHandler(actionSiteClass, method);
            }

            ActionPermission actionPermissionAnnt = method.getAnnotation(ActionPermission.class);

            if ((actionPermissionAnnt != null) && actionPermissionAnnt.value().equals(getId())) {
                if (isPermitMethodFound) {
                    throw new RuntimeException("Action site class can contains only one permission method per action" +
                            " [actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
                }

                isPermitMethodFound = true;

                checkPermissionMethod(actionSiteClass, method);
            }
        }

        if (!isHandlerFound) {
            throw new RuntimeException("Action site class should contains a handler for the action [" +
                    "actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
        }

        if (!isPermitMethodFound) {
            throw new RuntimeException("Action site class should contains a permission method for the action [" +
                    "actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
        }
    }

    private void checkActionHandler(Class<? extends ActionSite> actionSiteClass, Method method) {
        Class<?> returnType = method.getReturnType();

        boolean isValid = returnType.isAssignableFrom(void.class) && (method.getParameterTypes().length == 0);

        if (!isValid) {
            throw new RuntimeException("Action site class contains invalid handler [method = " + method +
                    "; actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
        }
    }

    private void checkPermissionMethod(Class<? extends ActionSite> actionSiteClass, Method method) {
        Class<?> returnType = method.getReturnType();

        boolean isValid = (returnType.isAssignableFrom(Boolean.class) || returnType.isAssignableFrom(boolean.class))
                && (method.getParameterTypes().length == 0);

        if (!isValid) {
            throw new RuntimeException("Action site class contains invalid permission method [method = " + method +
                    "; actionSiteClass = " + actionSiteClass + "; this = " + this + ']');
        }
    }

    private Method getDeclaredHandlerMethod() {
        List<Method> actionSiteMethods = asList(actionSiteClass.getDeclaredMethods());

        for (Method actionSiteMethod : actionSiteMethods) {
            ActionHandler actionHandlerAnnt = actionSiteMethod.getAnnotation(ActionHandler.class);

            if ((actionHandlerAnnt != null) && actionHandlerAnnt.value().equals(getId())) {
                return actionSiteMethod;
            }
        }

        throw new InternalError("Action site object should contain an action handler.");
    }

    private Method getDeclaredPermissionMethod() {
        List<Method> actionSiteMethods = asList(actionSiteClass.getDeclaredMethods());

        for (Method actionSiteMethod : actionSiteMethods) {
            ActionPermission actionPermissionAnnt = actionSiteMethod.getAnnotation(ActionPermission.class);

            if ((actionPermissionAnnt != null) && actionPermissionAnnt.value().equals(getId())) {
                return actionSiteMethod;
            }
        }

        throw new InternalError("Action site object should contain an action permission method.");
    }

    private void validateHandlerImplementor(ActionSite actionSite) {
        Method declaredHandlerMethod = getDeclaredHandlerMethod();

        String declaredHandlerMethodName = declaredHandlerMethod.getName();

        Method handlerMethodImplementor;
        
        try {
            handlerMethodImplementor = actionSite.getClass().getDeclaredMethod(declaredHandlerMethodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        ActionHandler actionHandlerAnnt = handlerMethodImplementor.getAnnotation(ActionHandler.class);

        if ((actionHandlerAnnt == null) || !actionHandlerAnnt.value().equals(getId())) {
            throw new IllegalArgumentException("Action site should has an annotated handler implementor" +
                    " [actionSite = " + actionSite + "; actionSiteClass = " + actionSiteClass + "; this = " + this +
                    ']');
        }
    }

    private void validatePermissionMethodImplementor(ActionSite actionSite) {
        Method declaredPermissionMethod = getDeclaredPermissionMethod();

        String declaredPermissionMethodName = declaredPermissionMethod.getName();

        Method permissionMethodImplementor;

        try {
            permissionMethodImplementor = actionSite.getClass().getDeclaredMethod(declaredPermissionMethodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        ActionPermission actionPermissionAnnt = permissionMethodImplementor.getAnnotation(ActionPermission.class);

        if ((actionPermissionAnnt == null) || !actionPermissionAnnt.value().equals(getId())) {
            throw new IllegalArgumentException("Action site should has an annotated permission method implementor" +
                    " [actionSite = " + actionSite + "; actionSiteClass = " + actionSiteClass + "; this = " + this +
                    ']');
        }
    }
}
