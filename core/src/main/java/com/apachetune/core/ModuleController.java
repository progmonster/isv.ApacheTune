package com.apachetune.core;

import com.google.inject.Module;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface ModuleController {
    /**
     * FIXDOC
     *
     * Module controller may add a module root work item (if exists) to a framework root work item or add to any other
     * work item from other modules.
     *
     * @param rootWorkItem FIXDOC
     */
    void initialize(RootWorkItem rootWorkItem);

    /**
     * FIXDOC
     *
     * This method should be able to be called before dependency injection his members and initialization method call.  
     *
     * @return A module instance or <code>null</code> if it has no one. This value should be able even before
     * {@link #initialize(RootWorkItem)} will be called.
     */
    Module getModule();
}
