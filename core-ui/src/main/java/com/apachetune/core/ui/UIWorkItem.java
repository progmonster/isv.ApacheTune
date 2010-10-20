package com.apachetune.core.ui;

import com.apachetune.core.WorkItem;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface UIWorkItem extends WorkItem {
    boolean needActionManagerAutobinding();

    void addPresenter(Object presenter);

    void removePresenter(Object presenter);    
}
