package com.apachetune.core;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class SimpleWorkItem extends GenericWorkItem {
    /**
     * FIXDOC
     *
     * @param id FIXDOC
     */
    public SimpleWorkItem(String id) {
        super(id);
    }

    protected void doInitialize() { }

    protected void doDispose() {
        // No-op.
    }
}
