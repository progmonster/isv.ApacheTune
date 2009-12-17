package com.apachetune.httpserver.ui;

import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.*;
import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.ui.resources.*;
import com.google.inject.*;
import com.google.inject.name.*;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ObjectTreeWorkItem extends GenericUIWorkItem implements FocusListener {
    private final ToolWindowManager toolWindowManager;

    private final HttpServerResourceLocator httpServerResourceLocator;

    @SuppressWarnings({"FieldCanBeLocal"}) // TODO remove
    private JTree objectTree;

    @Inject
    public ObjectTreeWorkItem(@Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            HttpServerResourceLocator httpServerResourceLocator) {
        super(OBJECT_TREE_WORK_ITEM);
        
        this.toolWindowManager = toolWindowManager;
        this.httpServerResourceLocator = httpServerResourceLocator;        
    }

    protected void doUIInitialize() {
        objectTree = new JTree();

        try {
            // TODO Localize
            toolWindowManager.registerToolWindow(OBJECT_TREE_TOOL_WINDOW, "Object tree", httpServerResourceLocator
                    .loadIcon("console_view_icon.png"), objectTree, LEFT);
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        ToolWindow window = toolWindowManager.getToolWindow(OBJECT_TREE_TOOL_WINDOW);

        window.setVisible(true);

        objectTree.addFocusListener(this);
    }

    protected void doUIDispose() {
        objectTree.removeFocusListener(this);

        toolWindowManager.unregisterToolWindow(OBJECT_TREE_TOOL_WINDOW);
    }

    public void focusGained(FocusEvent e) {
        activate();
    }

    public void focusLost(FocusEvent e) {
        // No-op.
    }
}
