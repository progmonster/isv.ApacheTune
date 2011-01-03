package com.apachetune.httpserver.ui;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.ui.GenericUIWorkItem;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.apachetune.core.ui.Constants.OBJECT_TREE_TOOL_WINDOW;
import static com.apachetune.core.ui.Constants.TOOL_WINDOW_MANAGER;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.httpserver.Constants.OBJECT_TREE_WORK_ITEM;
import static org.noos.xing.mydoggy.ToolWindowAnchor.LEFT;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class ObjectTreeWorkItem extends GenericUIWorkItem implements FocusListener {
    private final ToolWindowManager toolWindowManager;

    private final HttpServerResourceLocator httpServerResourceLocator;

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(ObjectTreeWorkItem.class);

    @SuppressWarnings({"FieldCanBeLocal"})
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
            //noinspection DuplicateStringLiteralInspection
            toolWindowManager.registerToolWindow(OBJECT_TREE_TOOL_WINDOW,
                    resourceBundle.getString("objectTreeWorkItem.objectTreeToolWindow"),
                    httpServerResourceLocator
                    .loadIcon("console_view_icon.png"), objectTree, LEFT); //NON-NLS
        } catch (IOException e) {
            throw createRuntimeException(e);
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
