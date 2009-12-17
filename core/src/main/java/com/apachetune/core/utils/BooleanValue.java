package com.apachetune.core.utils;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class BooleanValue extends Value<Boolean> {
    public BooleanValue() {
        super(false);
    }

    public BooleanValue(boolean value) {
        super(value);
    }
}
