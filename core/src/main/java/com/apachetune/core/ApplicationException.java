package com.apachetune.core;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ApplicationException extends Exception {
    private static final long serialVersionUID = -2166899409610457949L;

    public ApplicationException(String msg) {
        super(msg);
    }

    public ApplicationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
