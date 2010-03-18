package com.apachetune.httpserver.ui;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.ui.GenericUIWorkItem;
import com.apachetune.core.ui.MenuBarManager;
import com.apachetune.core.ui.OutputPaneDocument;
import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.editors.EditorActionSite;
import com.apachetune.httpserver.entities.HttpServer;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.URI;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.ui.Constants.*;
import static com.apachetune.httpserver.Constants.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.BOTTOM;
import static org.noos.xing.mydoggy.ToolWindowType.DOCKED;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
// TODO Move to Core-UI module.
public class ConsoleWorkItem extends GenericUIWorkItem implements EditorActionSite, FocusListener {
    private static final int MINIMAL_OUTPUT_WINDOW_DOCK_LENGTH = 100;

    private final ToolWindowManager toolWindowManager;

    private final OutputPaneDocument outputPaneDocument;

    private final HttpServerResourceLocator httpServerResourceLocator;

    private final MenuBarManager menuBarManager;
    
    private final PreferencesManager preferencesManager;

    private JTextPane stdoutPane;

    @Inject
    public ConsoleWorkItem(
            OutputPaneDocument outputPaneDocument,
            @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            HttpServerResourceLocator httpServerResourceLocator,
            MenuBarManager menuBarManager,
            PreferencesManager preferencesManager) {
        super(CONSOLE_WORK_ITEM);

        this.outputPaneDocument = outputPaneDocument;
        this.toolWindowManager = toolWindowManager;
        this.httpServerResourceLocator = httpServerResourceLocator;
        this.menuBarManager = menuBarManager;
        this.preferencesManager = preferencesManager;
    }

    @ActionHandler(EDIT_COPY_ACTION)
    public void onCopy() {
        stdoutPane.copy();
    }

    @ActionPermission(EDIT_COPY_ACTION)
    public boolean isCopyEnabled() {
        return true;
    }

    @ActionHandler(EDIT_CUT_ACTION)
    public void onCut() {
        // No-op
    }

    @ActionPermission(EDIT_CUT_ACTION)
    public boolean isCutEnabled() {
        return false;
    }

    @ActionHandler(EDIT_PASTE_ACTION)
    public void onPaste() {
        // No-op.
    }

    @ActionPermission(EDIT_PASTE_ACTION)
    public boolean isPasteEnabled() {
        return false;
    }

    @ActionHandler(EDIT_SELECT_ALL_ACTION)
    public void onSelectAll() {
        stdoutPane.grabFocus();

        stdoutPane.selectAll();
    }

    @ActionPermission(EDIT_SELECT_ALL_ACTION)
    public boolean isSelectAllEnabled() {
        return true;
    }

    public void focusGained(FocusEvent e) {
        activate();
    }

    public void focusLost(FocusEvent e) {
        // No-op.
    }

    // FIX restore focus for Console if server was closed when this console was active.

    protected void doActivation() {
        getOutputWindow().setSelected(true);

        getOutputWindow().setActive(true);
    }

    protected void doUIInitialize() {
        stdoutPane = new JTextPane();

        stdoutPane.setDocument(outputPaneDocument);
        stdoutPane.setText("");

        try {
            ToolWindowAnchor anchor = getRestoredOutputWindowAnchor();

            // TODO Localize
            toolWindowManager.registerToolWindow(OUTPUT_TOOL_WINDOW, "Output view", httpServerResourceLocator
                    .loadIcon("console_view_icon.png"), stdoutPane, anchor);
        } catch (IOException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        getOutputWindowDocketDescriptor().setMinimumDockLength(MINIMAL_OUTPUT_WINDOW_DOCK_LENGTH);
        getOutputWindowDocketDescriptor().setDockLength(getRestoredOutputWindowDockLength());
        getOutputWindow().setVisible(true);

        stdoutPane.addFocusListener(this);

        menuBarManager.createAndBindContextMenu(stdoutPane, this);
    }

    protected void doUIDispose() {
        storeOutputWindowDockLength();
        storeOutputWindowAnchor();

        stdoutPane.removeFocusListener(this);
        stdoutPane = null;

        toolWindowManager.unregisterToolWindow(OUTPUT_TOOL_WINDOW);
    }

    private DockedTypeDescriptor getOutputWindowDocketDescriptor() {
        return (DockedTypeDescriptor) getOutputWindow().getTypeDescriptor(DOCKED);
    }

    private MyDoggyToolWindow getOutputWindow() {
        return (MyDoggyToolWindow) toolWindowManager.getToolWindow(OUTPUT_TOOL_WINDOW);
    }

    private void storeOutputWindowDockLength() {
        Preferences pref = preferencesManager.userNodeForPackage(ConsoleWorkItem.class).node(
                OUTPUT_WINDOW_DOCK_LENGTH_PREF
        );

        pref.putInt(getHttpServerUri().toASCIIString(), getOutputWindowDocketDescriptor().getDockLength());

        try {
            pref.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();  // TODO Make it as a service.
        }
    }

    private void storeOutputWindowAnchor() {
        Preferences pref = preferencesManager.userNodeForPackage(ConsoleWorkItem.class).node(OUTPUT_WINDOW_ANCHOR_PREF);

        pref.put(getHttpServerUri().toASCIIString(), getOutputWindow().getAnchor().name());

        try {
            pref.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();  // TODO Make it as a service.
        }
    }

    public ToolWindowAnchor getRestoredOutputWindowAnchor() {
        String restoredAnchorName = preferencesManager.userNodeForPackage(ConsoleWorkItem.class).node(
                OUTPUT_WINDOW_ANCHOR_PREF).get(getHttpServerUri().toASCIIString(), BOTTOM.name());

        return ToolWindowAnchor.valueOf(restoredAnchorName);
    }

    private int getRestoredOutputWindowDockLength() {
        Preferences pref = preferencesManager.userNodeForPackage(ConsoleWorkItem.class).node(
                OUTPUT_WINDOW_DOCK_LENGTH_PREF
        );
        
        return pref.getInt(getHttpServerUri().toASCIIString(), MINIMAL_OUTPUT_WINDOW_DOCK_LENGTH);
    }

    private HttpServer getHttpServer() {
        return (HttpServer) getState(CURRENT_HTTP_SERVER_STATE);
    }

    private URI getHttpServerUri() {
        return getHttpServer().getUri();
    }
}
