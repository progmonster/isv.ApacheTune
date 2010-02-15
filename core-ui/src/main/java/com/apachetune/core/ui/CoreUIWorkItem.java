package com.apachetune.core.ui;

import com.apachetune.core.*;
import static com.apachetune.core.ui.Constants.*;
import static com.apachetune.core.ui.TitleBarManager.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.editors.*;
import com.apachetune.core.ui.resources.*;
import com.google.inject.*;
import com.google.inject.name.*;
import jsyntaxpane.*;
import jsyntaxpane.jsyntaxkits.*;
import static org.apache.commons.lang.StringUtils.*;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.content.*;
import org.noos.xing.mydoggy.plaf.*;

import javax.swing.*;
import static javax.swing.KeyStroke.*;
import java.awt.*;
import java.awt.event.*;
import static java.awt.event.InputEvent.CTRL_MASK;
import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.*;
import java.util.prefs.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CoreUIWorkItem extends GenericUIWorkItem implements ActivationListener {
    private final JFrame mainFrame;

    private final ToolWindowManager toolWindowManager;

    private final MenuBarManager menuBarManager;

    private final ActionManager actionManager;

    private final ToolBarManager toolBarManager;

    private final CoreUIUtils coreUIUtils;

    private final CoreUIResourceLocator coreUIResourceLocator;

    private final Rectangle mainFrameBounds = new Rectangle();

    private final StatusBarManager statusBarManager;

    private final AppManager appManager;

    private final TitleBarManager titleBarManager;

    @Inject
    public CoreUIWorkItem(JFrame mainFrame, @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            MenuBarManager menuBarManager, ActionManager actionManager, CoreUIUtils coreUIUtils,
            CoreUIResourceLocator coreUIResourceLocator, StatusBarManager statusBarManager,
            ToolBarManager toolBarManager, AppManager appManager, TitleBarManager titleBarManager) {
        super(CORE_UI_WORK_ITEM);

        this.mainFrame = mainFrame;
        this.toolWindowManager = toolWindowManager;
        this.menuBarManager = menuBarManager;
        this.actionManager = actionManager;
        this.coreUIUtils = coreUIUtils;
        this.coreUIResourceLocator = coreUIResourceLocator;
        this.toolBarManager = toolBarManager;
        this.statusBarManager = statusBarManager;
        this.appManager = appManager;
        this.titleBarManager = titleBarManager;
    }

    public void onActivate(WorkItem workItem) {
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        if (workItem instanceof UIWorkItem) {
            UIWorkItem uiWorkItem = (UIWorkItem) workItem;

            if (uiWorkItem.needActionManagerAutobinding()) {
                actionManager.activateActionSites(uiWorkItem);
            }
        }
    }

    public void onDeactivate(WorkItem workItem) {
        if (workItem == null) {
            throw new NullPointerException("Argument workItem cannot be a null [this = " + this + "]");
        }

        if (workItem instanceof UIWorkItem) {
            UIWorkItem uiWorkItem = (UIWorkItem) workItem;

            if (uiWorkItem.needActionManagerAutobinding()) {
                actionManager.deactivateActionSites(uiWorkItem);
            }
        }
    }

    protected void doUIInitialize() {
        raiseEvent(SHOW_SPLASH_SCREEN_EVENT);

        getRootWorkItem().addChildActivationListener(this);

        initMainFrame();
        initActions();
        initMenuBar();
        initToolBar();
        initStatusBar();
        initTitleBar();
        initDockingFramework();

        activate();

        DefaultSyntaxKit.initKit();
        DefaultSyntaxKit.registerContentType("text/plain", ExtendedSyntaxKit.class.getName());

        mainFrame.setVisible(true);
    }

    protected void doUIDispose() {
        disposeActions();

        try {
            storeMainFrameBounds();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        getRootWorkItem().removeChildActivationListener(this);

        mainFrame.setVisible(false);
        mainFrame.dispose();

        System.exit(0);
    }

    private void initDockingFramework() {
        ContentManager contentManager = toolWindowManager.getContentManager();

        MyDoggyTabbedContentManagerUI contentManagerUI = new MyDoggyTabbedContentManagerUI() {
            protected void setupActions() {
                // Workaround. Switch tab actions are configuring when ManagerUI being created and its toolWindowManager
                // property not set yet.
                toolWindowManager = (MyDoggyToolWindowManager) CoreUIWorkItem.this.toolWindowManager;

                super.setupActions();
            }
        };

        contentManager.setContentManagerUI(contentManagerUI);

        contentManager.setEnabled(true);

        mainFrame.getContentPane().add((Component) toolWindowManager);
    }

    private void disposeActions() {
        actionManager.unregisterActionGroup(CORE_UI_ACTION_GROUP);
    }

    private void initMainFrame() {
        restoreMainFrameBounds();

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        mainFrame.getContentPane().setLayout(new BorderLayout());

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                raiseEvent(EXIT_EVENT);
            }
        });

        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if ((mainFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) && (mainFrame.getExtendedState() != JFrame
                        .ICONIFIED)) {
                    mainFrameBounds.setLocation(mainFrame.getLocation());
                }
            }

            @Override
            public void componentResized(ComponentEvent e) {
                if ((mainFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) && (mainFrame.getExtendedState() != JFrame
                        .ICONIFIED)) {
                    mainFrameBounds.setSize(mainFrame.getSize());
                }
            }
        });
    }

    private void initTitleBar() {
        AppVersion version = appManager.getVersion();

        String appBuild = version.getBuild();

        String appTitle = appManager.getName() + ' ' + version.format("{major}.{minor} {Edition}");

        if (!defaultString(appBuild).isEmpty()) {
            appTitle += " [" + appBuild + ']';
        }

        titleBarManager.setTitle(LEVEL_1, appTitle);
    }

    private void restoreMainFrameBounds() {
        Preferences userNode = Preferences.userNodeForPackage(getClass());

        int mainFrameState = userNode.getInt(MAIN_FRAME_STATE_PERSISTED, JFrame.NORMAL);

        mainFrame.setExtendedState(mainFrameState != JFrame.ICONIFIED ? mainFrameState : JFrame.NORMAL);

        // TODO Set "beautiful" initial bounds.
        int left = userNode.getInt(MAIN_FRAME_LEFT_PERSISTED, 100);

        int top = userNode.getInt(MAIN_FRAME_TOP_PERSISTED, 100);

        int width = userNode.getInt(MAIN_FRAME_WIDTH_PERSISTED, 640);

        int height = userNode.getInt(MAIN_FRAME_HEIGHT_PERSISTED, 480);

        mainFrame.setBounds(left, top, width, height);

        mainFrameBounds.setBounds(left, top, width, height);
    }

    private void initMenuBar() {
        initFileMenu();
        initEditMenu();
        initHelpMenu();

        mainFrame.setJMenuBar(menuBarManager.getMenuBar());
    }

    private void initActions() {
        // TODO Use standard actions for undo, redo, cut, etc.

        ActionGroup coreUIActionGroup = actionManager.createActionGroup(CORE_UI_ACTION_GROUP);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(FILE_SAVE_ACTION, SaveFileActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Save", "Save editing file", "Save current editing file",
                "save_document_16.png", null, 'S', getKeyStroke(VK_S, CTRL_MASK), false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(FILE_SAVE_ALL_ACTION, SaveAllFilesActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Save all", "Save all editing file", "Save all editing file",
                "save_all_documents_16.png", null, 'S', getKeyStroke(VK_S, CTRL_MASK | SHIFT_MASK), false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(FILE_PRINT_ACTION, PrintDocumentActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Print...", "Print document", "Print current document", "print_16.png", null,
                'P', null, false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EXIT_ACTION, AppExitActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Exit", "Exit", "Exit from the application", null, null, 'x', null, false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_UNDO_ACTION, UndoWorkflowActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Undo", "Undo change", "Undo last change", "undo_16.png", null, 'U',
                getKeyStroke(VK_Z, CTRL_MASK), false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_REDO_ACTION, UndoWorkflowActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Redo", "Redo change", "Redo last change", "redo_16.png", null, 'R',
                getKeyStroke(VK_Z, CTRL_MASK | SHIFT_MASK), false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(HELP_ABOUT_ACTION, AboutActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "About", "About the application", "Show about dialog", "about_16.png", null,
                'A', null, false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(HELP_HELP_TOPICS_ACTION, HelpActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Help topics", "Help topics", "Show help topics", "help_16.png", null, 't',
                getKeyStroke(VK_F1, 0), false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(HELP_SUBMIT_FEEDBACK_ACTION, FeedbackActionSite.class,
                coreUIActionGroup, coreUIResourceLocator, "Submit feedback", "Submit feedback",
                "Submit feedback to the application developers", "send_feedback_16.png", null, 'f', null, false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(HELP_REGISTER_ACTION, RegisterActionSite.class, coreUIActionGroup,
                coreUIResourceLocator, "Register...", "Register", "Register the application", null, null, 'R',
                null, false);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(HELP_CHECK_FOR_UPDATE_ACTION, UpdateActionSite.class,
                coreUIActionGroup, coreUIResourceLocator, "Check for update...", "Check for update",
                "Check for update the application", null, null, 'C', null, false);

        actionManager.registerActionGroup(coreUIActionGroup);

        ActionGroup clipboardActionGroup = actionManager.createActionGroup(CLIPBOARD_ACTION_GROUP);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_CUT_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator, "Cut", "Cut selection", "Cut selection to clipboard", "cut_16.png", null,
                't', getKeyStroke(VK_X, CTRL_MASK), true);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_COPY_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator, "Copy", "Copy selection", "Copy selection to clipboard", "copy_16.png", null,
                'C', getKeyStroke(VK_C, CTRL_MASK), true);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_PASTE_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator, "Paste", "Paste", "Paste from clipboard", "paste_16.png", null, 'P',
                getKeyStroke(VK_V, CTRL_MASK), true);

        actionManager.registerActionGroup(clipboardActionGroup);

        ActionGroup selectAllActionGroup = actionManager.createActionGroup(SELECT_ALL_ACTION_GROUP);

        // TODO Localize.
        coreUIUtils.createAndConfigureAction(EDIT_SELECT_ALL_ACTION, EditorActionSite.class, selectAllActionGroup,
                coreUIResourceLocator, "Select all", "Select all", "Select all", null, null, 'a',
                getKeyStroke(VK_A, CTRL_MASK), true);

        actionManager.registerActionGroup(selectAllActionGroup);
    }

    private void initStatusBar() {
        statusBarManager.initialize();

        statusBarManager.addMainStatus(READY_STATUS, "Ready"); // TODO Localize.
    }

    private void initToolBar() {
        toolBarManager.initialize();

        toolBarManager.addActionGroup(FILE_ACTION_GROUP, actionManager.getAction(FILE_SAVE_ALL_ACTION), actionManager
                .getAction(FILE_SAVE_ACTION));

        toolBarManager.addActionGroup(PRINT_ACTION_GROUP, actionManager.getAction(FILE_PRINT_ACTION));


        toolBarManager.addActionGroup(UNDO_REDO_ACTION_GROUP, actionManager.getAction(EDIT_UNDO_ACTION), actionManager
                .getAction(EDIT_REDO_ACTION));

        toolBarManager.addActionGroup(EDIT_ACTION_GROUP, actionManager.getAction(EDIT_CUT_ACTION), actionManager
                .getAction(EDIT_COPY_ACTION), actionManager.getAction(EDIT_PASTE_ACTION));

        toolBarManager.addActionGroup(FEEDBACK_ACTION_GROUP, actionManager.getAction(HELP_SUBMIT_FEEDBACK_ACTION));

        toolBarManager.addActionGroup(HELP_ACTION_GROUP, actionManager.getAction(HELP_HELP_TOPICS_ACTION));
    }

    private void initFileMenu() {
        JMenu fileMenu = new JMenu("File"); // TODO Localize. Add an accelerator.

        fileMenu.setMnemonic('F'); // TODO Localize. 

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_SAVE_ACTION)));

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_SAVE_ALL_ACTION)));

        fileMenu.addSeparator();

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_PRINT_ACTION)));

        fileMenu.addSeparator();
                      
        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(EXIT_ACTION)));

        menuBarManager.addMenu(FILE_MENU, fileMenu);
    }

    private void initEditMenu() {
        JMenu editMenu = new JMenu("Edit"); // TODO Localize. Add an accelerator.

        editMenu.setMnemonic('E'); // TODO Localize.

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_UNDO_ACTION)));

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_REDO_ACTION)));

        editMenu.addSeparator();

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_CUT_ACTION)));

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_COPY_ACTION)));

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_PASTE_ACTION)));

        editMenu.addSeparator();

        coreUIUtils.addUIActionHint(editMenu.add(actionManager.getAction(EDIT_SELECT_ALL_ACTION)));

        menuBarManager.addMenu(EDIT_MENU, editMenu);
    }

    private void initHelpMenu() {
        JMenu helpMenu = new JMenu("Help"); // TODO Localize. Add an accelerator.

        helpMenu.setMnemonic('H'); // TODO Localize. 

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_HELP_TOPICS_ACTION)));

        helpMenu.addSeparator();

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_SUBMIT_FEEDBACK_ACTION)));

        helpMenu.addSeparator();

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_REGISTER_ACTION)));

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_CHECK_FOR_UPDATE_ACTION)));

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_ABOUT_ACTION)));

        menuBarManager.addMenu(HELP_MENU, helpMenu);
    }

    private void storeMainFrameBounds() throws BackingStoreException {
        Preferences userNode = Preferences.userNodeForPackage(getClass());

        userNode.putInt(MAIN_FRAME_STATE_PERSISTED, mainFrame.getExtendedState());

        Rectangle bounds = ((mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) || (mainFrame.getExtendedState() ==
                JFrame.ICONIFIED)) ? mainFrameBounds : mainFrame.getBounds();

        userNode.putInt(MAIN_FRAME_LEFT_PERSISTED, (int) bounds.getX());
        userNode.putInt(MAIN_FRAME_TOP_PERSISTED, (int) bounds.getY());
        userNode.putInt(MAIN_FRAME_WIDTH_PERSISTED, (int) bounds.getWidth());
        userNode.putInt(MAIN_FRAME_HEIGHT_PERSISTED, (int) bounds.getHeight());
        
        userNode.flush();
    }
}
