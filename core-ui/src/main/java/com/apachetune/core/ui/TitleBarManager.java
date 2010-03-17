package com.apachetune.core.ui;

import static java.lang.Integer.MAX_VALUE;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface TitleBarManager {
    public static final int LEVEL_1 = 0;

    public static final int LEVEL_2 = 1;

    public static final int LEVEL_3 = 2;

    public static final int LEVEL_4 = 3;

    public static final int LEVEL_LAST = MAX_VALUE;

    void setTitle(int level, String title);

    void removeTitle(int level); 
}
