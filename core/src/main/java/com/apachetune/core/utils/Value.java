package com.apachetune.core.utils;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class Value<T> {
    public T value;

    public Value() {
        // No-op.
    }

    public Value(T value) {
        this.value = value;
    }
}
