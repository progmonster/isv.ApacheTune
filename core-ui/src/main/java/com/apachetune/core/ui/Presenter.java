package com.apachetune.core.ui;

import com.apachetune.core.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface Presenter<TView> {
    void initialize(WorkItem workItem, TView view);
}
