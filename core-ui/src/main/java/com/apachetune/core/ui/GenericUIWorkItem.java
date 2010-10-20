package com.apachetune.core.ui;

import com.apachetune.core.GenericWorkItem;
import com.apachetune.core.WorkItem;

import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public abstract class GenericUIWorkItem extends GenericWorkItem implements UIWorkItem {
    private List<Object> presenters = new ArrayList<Object>();

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

    @Override
    public final void addPresenter(Object presenter) {
        notNull(presenter);
        isTrue(!presenters.contains(presenter));

        presenters.add(presenter);
    }

    @Override
    public final void removePresenter(Object presenter) {
        notNull(presenter);

        presenters.remove(presenter);
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

    @Override
    protected void onRaiseEvent(String eventId, Object data, WorkItem caller) {
        List presentersCopiedList = new ArrayList<Object>(presenters);

        for (Object presenter : presentersCopiedList) {
            callObjectHandlers(presenter, eventId, data);
        }        
    }

    protected abstract void doUIDispose();

    protected abstract void doUIInitialize();
}
