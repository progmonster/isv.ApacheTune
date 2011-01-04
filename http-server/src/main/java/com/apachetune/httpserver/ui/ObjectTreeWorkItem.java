package com.apachetune.httpserver.ui;

import com.apachetune.core.ResourceManager;
import com.apachetune.core.ui.GenericUIWorkItem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import static com.apachetune.core.ui.Constants.OBJECT_TREE_TOOL_WINDOW;
import static com.apachetune.core.ui.Constants.TOOL_WINDOW_MANAGER;
import static com.apachetune.core.utils.Utils.loadIcon;
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

    private final ResourceBundle resourceBundle =
            ResourceManager.getInstance().getResourceBundle(ObjectTreeWorkItem.class);

    @SuppressWarnings({"FieldCanBeLocal"})
    private JTree objectTree;

    @Inject
    public ObjectTreeWorkItem(@Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager) {
        super(OBJECT_TREE_WORK_ITEM);
        
        this.toolWindowManager = toolWindowManager;
    }

    protected void doUIInitialize() {
        objectTree = new JTree();

        //noinspection DuplicateStringLiteralInspection
        toolWindowManager.registerToolWindow(OBJECT_TREE_TOOL_WINDOW,
                resourceBundle.getString("objectTreeWorkItem.objectTreeToolWindow"),
                loadIcon(ObjectTreeWorkItem.class, "console_view_icon.png"), objectTree, LEFT); //NON-NLS

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
