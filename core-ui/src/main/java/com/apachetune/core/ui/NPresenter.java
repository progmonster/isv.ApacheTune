package com.apachetune.core.ui;

import com.apachetune.core.WorkItem;

import static org.apache.commons.lang.Validate.notNull;

public abstract class NPresenter<TView extends NView> {
    private boolean disposed = false;

    private TView view;

    private UIWorkItem workItem;

    public final void initialize(UIWorkItem workItem, TView view) {
        notNull(workItem);
        notNull(view);

        this.view = view;
        this.workItem = workItem;

        workItem.addPresenter(this);
    }

    public abstract void onViewReady();

    public abstract void onCloseView();

    public final void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;

        view.dispose();

        workItem.removePresenter(this);
    }

    protected final TView getView() {
        return view;
    }

    public final WorkItem getWorkItem() {
        return workItem;
    }
}
