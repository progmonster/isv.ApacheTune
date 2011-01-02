package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.TitleBarManager;
import com.google.inject.Inject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.Validate.isTrue;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class TitleBarManagerImpl implements TitleBarManager {
    private static final String TITLE_SEPARATOR = " - ";

    private final JFrame mainFrame;

    private final Map<Integer, String> titles = new HashMap<Integer, String>();

    @Inject
    public TitleBarManagerImpl(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setTitle(int level, String title) {
        //noinspection DuplicateStringLiteralInspection
        isTrue(level >= LEVEL_1,
                "Argument level cannot be less than LEVEL_1 [level = " + level + "; this = " + this + "]"); //NON-NLS

        if (defaultString(title).isEmpty()) {
            removeTitle(level);
        } else {
            titles.put(level, title);
            updateTitles();
        }
    }

    public void removeTitle(int level) {
        //noinspection DuplicateStringLiteralInspection
        isTrue(level >= LEVEL_1,
                "Argument level cannot be less than LEVEL_1 [level = " + level + "; this = " + this + "]"); //NON-NLS

        titles.remove(level);
        updateTitles();
    }

    private void updateTitles() {
        List<Integer> levels = new ArrayList<Integer>(titles.keySet());

        sort(levels);

        StringBuilder buf = new StringBuilder();

        for (Integer level : levels) {
            String title = titles.get(level);

            if (buf.length() > 0) {
                buf.append(TITLE_SEPARATOR);
            }

            buf.append(title);
        }

        mainFrame.setTitle(buf.toString());
    }
}
