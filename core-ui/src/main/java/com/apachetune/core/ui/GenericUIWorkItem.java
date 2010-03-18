package com.apachetune.core.ui;

import com.apachetune.core.GenericWorkItem;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
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
        if (!isEventDispatchThread()) {
            invokeLater(new Runnable() {
                public void run() {
                    doUIInitialize();
                }
            });
        } else {
            doUIInitialize();
        }
    }

    protected final void doDispose() {
        if (!isEventDispatchThread()) {
            invokeLater(new Runnable() {
                public void run() {
                    doUIDispose();
                }
            });
        } else {
            doUIDispose();
        }
    }

    protected abstract void doUIDispose();

    protected abstract void doUIInitialize();
}
