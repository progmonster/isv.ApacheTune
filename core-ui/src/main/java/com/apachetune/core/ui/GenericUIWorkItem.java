package com.apachetune.core.ui;

import com.apachetune.core.*;

import javax.swing.*;
import java.lang.reflect.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public abstract class GenericUIWorkItem extends GenericWorkItem implements UIWorkItem {
    public GenericUIWorkItem() {
        // No-op.
    }

    /**
     * FIXDOC
     *
     * @param id FIXDOC
     */
    public GenericUIWorkItem(String id) {
        super(id);
    }

    public boolean needActionManagerAutobinding() {
        return true;
    }

    protected final void doInitialize() {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        doUIInitialize();
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            }
        } else {
            doUIInitialize();
        }
    }

    protected final void doDispose() {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        doUIDispose();
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            }
        } else {
            doUIDispose();
        }
    }

    protected abstract void doUIDispose();

    protected abstract void doUIInitialize();
}
