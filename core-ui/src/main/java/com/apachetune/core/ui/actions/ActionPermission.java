package com.apachetune.core.ui.actions;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ActionPermission {
    String value();
}
