package com.apachetune.core.ui;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import com.apachetune.core.*;
import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.editors.EditorActionSite;
import com.apachetune.core.ui.feedbacksystem.UserFeedbackManager;
import com.apachetune.core.ui.resources.CoreUIResourceLocator;
import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.jsyntaxkits.ExtendedSyntaxKit;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;

import static com.apachetune.core.ui.Constants.*;
import static com.apachetune.core.ui.Constants.DONATE_ACTION_GROUP;
import static com.apachetune.core.ui.Constants.HELP_DONATE_ACTION;
import static com.apachetune.core.ui.TitleBarManager.LEVEL_1;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.apachetune.core.utils.Utils.openExternalWebPage;
import static java.awt.Frame.NORMAL;
import static java.awt.event.InputEvent.CTRL_MASK;
import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.*;
import static java.lang.Math.sqrt;
import static javax.swing.JFrame.*;
import static javax.swing.KeyStroke.getKeyStroke;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CoreUIWorkItem extends GenericUIWorkItem implements ActivationListener, FeedbackActionSite,
        DonateActionSite {
    private final ResourceBundle resourceBundle = ResourceManager.getInstance().getResourceBundle(CoreUIWorkItem.class);            

    private final JFrame mainFrame;

    private final SwingMaxWindowPatch swingMaxWindowPatch = new SwingMaxWindowPatch();

    private final ToolWindowManager toolWindowManager;

    private final MenuBarManager menuBarManager;

    private final ActionManager actionManager;

    private final ToolBarManager toolBarManager;

    private final CoreUIUtils coreUIUtils;

    private final CoreUIResourceLocator coreUIResourceLocator;

    private final Rectangle normalFrameBounds = new Rectangle();

    private final StatusBarManager statusBarManager;

    private final AppManager appManager;

    private final TitleBarManager titleBarManager;

    private final PreferencesManager preferencesManager;

    private final UserFeedbackManager userFeedbackManager;

    private JPanel mainPanel;

    @Inject
    public CoreUIWorkItem(
            JFrame mainFrame, @Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            MenuBarManager menuBarManager, ActionManager actionManager, CoreUIUtils coreUIUtils,
            CoreUIResourceLocator coreUIResourceLocator, StatusBarManager statusBarManager,
            ToolBarManager toolBarManager,
            AppManager appManager,
            TitleBarManager titleBarManager,
            PreferencesManager preferencesManager, UserFeedbackManager userFeedbackManager) {
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
        this.preferencesManager = preferencesManager;
        this.userFeedbackManager = userFeedbackManager;
    }

    @Subscriber(eventId = SEND_FEEDBACK_EVENT)
    @ActionHandler(HELP_SUBMIT_FEEDBACK_ACTION)
    @Override
    public final void onSendFeedback() {
        userFeedbackManager.sendUserFeedback();
    }

    @ActionPermission(HELP_SUBMIT_FEEDBACK_ACTION)
    @Override
    public final boolean isFeedbackEnabled() {
        return true;
    }

    @Subscriber(eventId = OPEN_WEB_PORTAL_DONATE_PAGE_EVENT)
    @ActionHandler(HELP_DONATE_ACTION)
    @Override
    public void onOpenWebPortalDonatePage() {
        String webPortalBaseUri = appManager.getProductWebPortalUri();

        if (webPortalBaseUri.charAt(webPortalBaseUri.length() - 1) != '/') { //NON-NLS
            webPortalBaseUri += '/'; //NON-NLS
        }

        openExternalWebPage(mainFrame, webPortalBaseUri + "donate"); //NON-NLS
    }

    @ActionPermission(HELP_DONATE_ACTION)
    @Override
    public boolean isOpenWebPortalDonatePageEnabled() {
        return true;
    }

    public final JFrame getMainFrame() {
        return mainFrame;
    }

    public void onActivate(WorkItem workItem) {
        //noinspection DuplicateStringLiteralInspection
        notNull(workItem, "Argument workItem cannot be a null"); //NON-NLS

        if (workItem instanceof UIWorkItem) {
            UIWorkItem uiWorkItem = (UIWorkItem) workItem;

            if (uiWorkItem.needActionManagerAutobinding()) {
                actionManager.activateActionSites(uiWorkItem);
            }
        }
    }

    public void onDeactivate(WorkItem workItem) {
        //noinspection DuplicateStringLiteralInspection
        notNull(workItem, "Argument workItem cannot be a null"); //NON-NLS

        if (workItem instanceof UIWorkItem) {
            UIWorkItem uiWorkItem = (UIWorkItem) workItem;

            if (uiWorkItem.needActionManagerAutobinding()) {
                actionManager.deactivateActionSites(uiWorkItem);
            }
        }
    }

    public void switchToToolWindowManager() {
        if (mainPanel.isAncestorOf((Component) toolWindowManager)) {
            return;
        }

        mainPanel.removeAll();

        mainPanel.add((Component) toolWindowManager);
    }

    public void switchToWelcomeScreen(final JPanel welcomeScreenPanel) {
        notNull(welcomeScreenPanel, "Argument welcomeScreenPanel cannot be a null"); //NON-NLS

        if (mainPanel.isAncestorOf(welcomeScreenPanel)) {
            return;
        }

        mainPanel.removeAll();

        mainPanel.add(welcomeScreenPanel);
    }

    protected void doUIInitialize() {
        raiseEvent(SHOW_SPLASH_SCREEN_EVENT);

        getRootWorkItem().addChildActivationListener(this);

        initMainFrame();
        initNativeSwingLibrary();

        initActions();
        initMenuBar();
        initToolBar();
        initStatusBar();
        initTitleBar();
        initDockingFramework();

        activate();

        DefaultSyntaxKit.initKit();
        //noinspection DuplicateStringLiteralInspection
        DefaultSyntaxKit.registerContentType("text/plain", ExtendedSyntaxKit.class.getName()); //NON-NLS

        mainFrame.setVisible(true);
    }

    protected void doUIDispose() {
        disposeActions();

        try {
            storeMainFrameBounds();
        } catch (BackingStoreException e) {
            throw createRuntimeException(e);
        }

        getRootWorkItem().removeChildActivationListener(this);

        mainFrame.getContentPane().remove(mainPanel);

        mainFrame.setVisible(false);
        mainFrame.dispose();

        System.exit(0);
    }

    private void initDockingFramework() {
        ContentManager contentManager = toolWindowManager.getContentManager();

        MyDoggyTabbedContentManagerUI contentManagerUI = new MyDoggyTabbedContentManagerUI() {
            @Override
            protected void setupActions() {
                // Workaround. Switch tab actions are configuring when ManagerUI being created and its toolWindowManager
                // property not set yet.
                toolWindowManager = (MyDoggyToolWindowManager) CoreUIWorkItem.this.toolWindowManager;

                super.setupActions();
            }
        };

        contentManager.setContentManagerUI(contentManagerUI);

        contentManager.setEnabled(true);

        mainPanel.add((Component) toolWindowManager);
    }

    private void disposeActions() {
        actionManager.unregisterActionGroup(CORE_UI_ACTION_GROUP);
    }

    private void initMainFrame() {
        restoreMainFrameBounds();

        mainFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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
                normalFrameBounds.setBounds(swingMaxWindowPatch.GetNormalBoundsAfterChangeLocationEvent(
                        normalFrameBounds));
            }

            @Override
            public void componentResized(ComponentEvent e) {
                normalFrameBounds.setBounds(swingMaxWindowPatch.GetNormalBoundsAfterChangeSizeEvent(normalFrameBounds));
            }
        });

        mainPanel = new JPanel();

        mainPanel.setLayout(new GridLayout());

        mainFrame.getContentPane().add(mainPanel);
    }

    private void initTitleBar() {
        AppVersion version = appManager.getVersion();

        String appBuild = version.getBuild();

        String appTitle = appManager.getName() + ' ' + version.format("{major}.{minor} {Edition}"); //NON-NLS

        if (!defaultString(appBuild).isEmpty()) {
            appTitle += " [" + appBuild + ']';
        }

        titleBarManager.setTitle(LEVEL_1, appTitle);
    }

    private void restoreMainFrameBounds() {
        Preferences userNode = preferencesManager.userNodeForPackage(getClass());

        final boolean isFrameMaximizied = userNode.getBoolean(MAIN_FRAME_MAXIMIZED_PERSISTED, false);

        Rectangle initialBounds = getInitialBounds();

        final Rectangle bounds = new Rectangle();

        bounds.x = userNode.getInt(MAIN_FRAME_LEFT_PERSISTED, initialBounds.x);

        bounds.y = userNode.getInt(MAIN_FRAME_TOP_PERSISTED, initialBounds.y);

        bounds.width = userNode.getInt(MAIN_FRAME_WIDTH_PERSISTED, initialBounds.width);

        bounds.height = userNode.getInt(MAIN_FRAME_HEIGHT_PERSISTED, initialBounds.height);

        normalFrameBounds.setBounds(bounds);

        mainFrame.setBounds(bounds);

        mainFrame.setExtendedState(isFrameMaximizied ? MAXIMIZED_BOTH : NORMAL);
    }

    private Rectangle getInitialBounds() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double GOLDEN_RATION = (1.0 + sqrt(5.0)) / 2.0;

        double INITIAL_PERCENT_OF_SCREEN_SQUARE_FRAME_SIZE = 100.0 / GOLDEN_RATION;

        double initialFrameSquare = screenSize.width * screenSize.height * INITIAL_PERCENT_OF_SCREEN_SQUARE_FRAME_SIZE
                / 100.0;

        Dimension initialFrameSize = new Dimension((int) (sqrt(initialFrameSquare * GOLDEN_RATION)),
                                                   (int) (sqrt(initialFrameSquare / GOLDEN_RATION)));

        if (initialFrameSize.width - screenSize.width >= -0.001) {
            initialFrameSize.width = (int) (initialFrameSize.width * (100.0 - 5.0) / 100.0); 
            initialFrameSize.height = (int) (initialFrameSize.width * (100.0 - 5.0) / 100.0);
        }

        if (initialFrameSize.height - screenSize.height >= -0.001) {
            initialFrameSize.width = (int) (initialFrameSize.width * (100.0 - 5.0) / 100.0);
            initialFrameSize.height = (int) (initialFrameSize.width * (100.0 - 5.0) / 100.0);
        }

        Point initialFrameLocation = new Point((int) ((screenSize.width - initialFrameSize.width) / 2.0),
                                               (int) ((screenSize.height - initialFrameSize.height) / 2.0));

        return new Rectangle(initialFrameLocation, initialFrameSize);
    }

    private void initMenuBar() {
        initFileMenu();
        initEditMenu();
        initWindowMenu();
        initHelpMenu();

        mainFrame.setJMenuBar(menuBarManager.getMenuBar());
    }

    private void initActions() {
        // TODO Use standard actions for undo, redo, cut, etc.

        ActionGroup coreUIActionGroup = actionManager.createActionGroup(CORE_UI_ACTION_GROUP);

        coreUIUtils.createAndConfigureAction(FILE_SAVE_ACTION, SaveFileActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.fileSave.name"),
                resourceBundle.getString("coreUIWorkItem.action.fileSave.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.fileSave.longDescription"),
                "save_document_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.fileSave.mnemonicKey").charAt(0),
                getKeyStroke(VK_S, CTRL_MASK), false);

        coreUIUtils.createAndConfigureAction(FILE_SAVE_ALL_ACTION, SaveAllFilesActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.fileSaveAll.name"),
                resourceBundle.getString("coreUIWorkItem.action.fileSaveAll.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.fileSaveAll.longDescription"),
                "save_all_documents_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.fileSaveAll.mnemonicKey").charAt(0),
                getKeyStroke(VK_S, CTRL_MASK | SHIFT_MASK), false);

        coreUIUtils.createAndConfigureAction(FILE_PRINT_ACTION, PrintDocumentActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.filePrint.name"),
                resourceBundle.getString("coreUIWorkItem.action.filePrint.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.filePrint.longDescription"),
                "print_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.filePrint.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(EXIT_ACTION, AppExitActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.exit.name"),
                resourceBundle.getString("coreUIWorkItem.action.exit.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.exit.longDescription"),
                null, null,
                resourceBundle.getString("coreUIWorkItem.action.exit.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(EDIT_UNDO_ACTION, UndoWorkflowActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editUndo.name"),
                resourceBundle.getString("coreUIWorkItem.action.editUndo.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editUndo.longDescription"),
                "undo_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.editUndo.mnemonicKey").charAt(0),
                getKeyStroke(VK_Z, CTRL_MASK), false);

        coreUIUtils.createAndConfigureAction(EDIT_REDO_ACTION, UndoWorkflowActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editRedo.name"),
                resourceBundle.getString("coreUIWorkItem.action.editRedo.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editRedo.longDescription"),
                "redo_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.editRedo.mnemonicKey").charAt(0),
                getKeyStroke(VK_Z, CTRL_MASK | SHIFT_MASK), false);

        coreUIUtils.createAndConfigureAction(HELP_ABOUT_ACTION, AboutActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpAbout.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpAbout.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpAbout.longDescription"),
                "about_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.helpAbout.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(HELP_HELP_TOPICS_ACTION, HelpActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpHelpTopics.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpHelpTopics.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpHelpTopics.longDescription"),
                "help_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.helpHelpTopics.mnemonicKey").charAt(0),
                getKeyStroke(VK_F1, 0), false);

        coreUIUtils.createAndConfigureAction(HELP_SUBMIT_FEEDBACK_ACTION, FeedbackActionSite.class,
                coreUIActionGroup, coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpSubmitFeedback.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpSubmitFeedback.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpSubmitFeedback.longDescription"),
                "send_feedback_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.helpSubmitFeedback.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(HELP_REGISTER_ACTION, RegisterActionSite.class, coreUIActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpRegister.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpRegister.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpRegister.longDescription"),
                null, null,
                resourceBundle.getString("coreUIWorkItem.action.helpRegister.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(HELP_CHECK_FOR_UPDATE_ACTION, UpdateActionSite.class,
                coreUIActionGroup, coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpCheckForUpdate.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpCheckForUpdate.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpCheckForUpdate.longDescription"),
                null, null,
                resourceBundle.getString("coreUIWorkItem.action.helpCheckForUpdate.mnemonicKey").charAt(0),
                null, false);

        coreUIUtils.createAndConfigureAction(HELP_DONATE_ACTION, DonateActionSite.class,
                coreUIActionGroup, coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.helpDonate.name"),
                resourceBundle.getString("coreUIWorkItem.action.helpDonate.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.helpDonate.longDescription"),
                "donate_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.helpDonate.mnemonicKey").charAt(0),
                null, false);

        actionManager.registerActionGroup(coreUIActionGroup);

        ActionGroup clipboardActionGroup = actionManager.createActionGroup(CLIPBOARD_ACTION_GROUP);

        coreUIUtils.createAndConfigureAction(EDIT_CUT_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editCut.name"),
                resourceBundle.getString("coreUIWorkItem.action.editCut.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editCut.longDescription"),
                "cut_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.editCut.mnemonicKey").charAt(0),
                getKeyStroke(VK_X, CTRL_MASK), true);

        coreUIUtils.createAndConfigureAction(EDIT_COPY_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editCopy.name"),
                resourceBundle.getString("coreUIWorkItem.action.editCopy.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editCopy.longDescription"),
                "copy_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.editCopy.mnemonicKey").charAt(0),
                getKeyStroke(VK_C, CTRL_MASK), true);

        coreUIUtils.createAndConfigureAction(EDIT_PASTE_ACTION, EditorActionSite.class, clipboardActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editPaste.name"),
                resourceBundle.getString("coreUIWorkItem.action.editPaste.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editPaste.longDescription"),
                "paste_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.editPaste.mnemonicKey").charAt(0),
                getKeyStroke(VK_V, CTRL_MASK), true);

        actionManager.registerActionGroup(clipboardActionGroup);

        ActionGroup selectAllActionGroup = actionManager.createActionGroup(SELECT_ALL_ACTION_GROUP);

        coreUIUtils.createAndConfigureAction(EDIT_SELECT_ALL_ACTION, EditorActionSite.class, selectAllActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.editSelectAll.name"),
                resourceBundle.getString("coreUIWorkItem.action.editSelectAll.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.editSelectAll.longDescription"),
                null, null,
                resourceBundle.getString("coreUIWorkItem.action.editSelectAll.mnemonicKey").charAt(0),
                getKeyStroke(VK_A, CTRL_MASK), true);

        actionManager.registerActionGroup(selectAllActionGroup);

        ActionGroup windowActionGroup = actionManager.createActionGroup(WINDOW_ACTION_GROUP);

        coreUIUtils.createAndConfigureAction(WINDOW_SELECT_NEXT_TAB_ACTION, WindowActionSite.class, windowActionGroup,
                coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.windowSelectNextTab.name"),
                resourceBundle.getString("coreUIWorkItem.action.windowSelectNextTab.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.windowSelectNextTab.longDescription"),
                "next_window_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.windowSelectNextTab.mnemonicKey").charAt(0),
                getKeyStroke(VK_RIGHT, ALT_MASK), true);

        coreUIUtils.createAndConfigureAction(WINDOW_SELECT_PREVIOUS_TAB_ACTION, WindowActionSite.class,
                windowActionGroup, coreUIResourceLocator,
                resourceBundle.getString("coreUIWorkItem.action.windowSelectPreviousTab.name"),
                resourceBundle.getString("coreUIWorkItem.action.windowSelectPreviousTab.shortDescription"),
                resourceBundle.getString("coreUIWorkItem.action.windowSelectPreviousTab.longDescription"),
                "previous_window_16.png", //NON-NLS
                null,
                resourceBundle.getString("coreUIWorkItem.action.windowSelectPreviousTab.mnemonicKey").charAt(0),
                getKeyStroke(VK_LEFT, ALT_MASK), true);

        actionManager.registerActionGroup(windowActionGroup);
    }

    private void initStatusBar() {
        statusBarManager.initialize();

        statusBarManager.addMainStatus(READY_STATUS,
                resourceBundle.getString("coreUIWorkItem.statusBarManager.readyStatus"));
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

        toolBarManager.addActionGroup(DONATE_ACTION_GROUP, actionManager.getAction(HELP_DONATE_ACTION));

/*
        todo uncomment
        toolBarManager.addActionGroup(HELP_ACTION_GROUP, actionManager.getAction(HELP_HELP_TOPICS_ACTION));

*/
    }

    private void initFileMenu() {
        JMenu fileMenu = new JMenu(resourceBundle.getString("coreUIWorkItem.menu.file.name"));

        fileMenu.setMnemonic(resourceBundle.getString("coreUIWorkItem.menu.file.mnemonicKey").charAt(0));

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_SAVE_ACTION)));

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_SAVE_ALL_ACTION)));

        fileMenu.addSeparator();

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(FILE_PRINT_ACTION)));

        fileMenu.addSeparator();

        coreUIUtils.addUIActionHint(fileMenu.add(actionManager.getAction(EXIT_ACTION)));

        menuBarManager.addMenu(FILE_MENU, fileMenu);
    }

    private void initEditMenu() {
        JMenu editMenu = new JMenu(resourceBundle.getString("coreUIWorkItem.menu.edit.name"));

        editMenu.setMnemonic(resourceBundle.getString("coreUIWorkItem.menu.edit.mnemonicKey").charAt(0));

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

    private void initWindowMenu() {
        JMenu windowMenu = new JMenu(resourceBundle.getString("coreUIWorkItem.menu.window.name"));

        windowMenu.setMnemonic(resourceBundle.getString("coreUIWorkItem.menu.window.mnemonicKey").charAt(0));

        coreUIUtils.addUIActionHint(windowMenu.add(actionManager.getAction(WINDOW_SELECT_NEXT_TAB_ACTION)));

        coreUIUtils.addUIActionHint(windowMenu.add(actionManager.getAction(WINDOW_SELECT_PREVIOUS_TAB_ACTION)));

        menuBarManager.addMenu(WINDOW_MENU, windowMenu);
    }

    private void initHelpMenu() {
        JMenu helpMenu = new JMenu(resourceBundle.getString("coreUIWorkItem.menu.help.name"));

        helpMenu.setMnemonic(resourceBundle.getString("coreUIWorkItem.menu.help.mnemonicKey").charAt(0)); 

/*        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_HELP_TOPICS_ACTION)));

        helpMenu.addSeparator(); todo uncomment*/

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_SUBMIT_FEEDBACK_ACTION)));

        helpMenu.addSeparator();

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_DONATE_ACTION)));

        helpMenu.addSeparator();
/*
        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_REGISTER_ACTION)));
        todo uncomment
*/

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_CHECK_FOR_UPDATE_ACTION)));

        coreUIUtils.addUIActionHint(helpMenu.add(actionManager.getAction(HELP_ABOUT_ACTION)));

        menuBarManager.addMenu(HELP_MENU, helpMenu);
    }

    private void storeMainFrameBounds() throws BackingStoreException {
        Preferences userNode = preferencesManager.userNodeForPackage(getClass());

        userNode.putBoolean(MAIN_FRAME_MAXIMIZED_PERSISTED, isFrameMaximized());

        userNode.putInt(MAIN_FRAME_LEFT_PERSISTED, (int) normalFrameBounds.getX());
        userNode.putInt(MAIN_FRAME_TOP_PERSISTED, (int) normalFrameBounds.getY());
        userNode.putInt(MAIN_FRAME_WIDTH_PERSISTED, (int) normalFrameBounds.getWidth());
        userNode.putInt(MAIN_FRAME_HEIGHT_PERSISTED, (int) normalFrameBounds.getHeight());
        
        userNode.flush();
    }

    private boolean isFrameIconified() {
        return (mainFrame.getExtendedState() & ICONIFIED) != 0;
    }

    private boolean isFrameMaximized() {
        return (mainFrame.getExtendedState() & MAXIMIZED_BOTH) != 0;
    }

    private boolean isNormalFrameState() {
        return !isFrameMaximized() && !isFrameIconified(); 
    }

    private void initNativeSwingLibrary() {
        NativeInterface.open();
    }

    /* PATCH: Этот патч позволяет избежать ошибку, связанную с выдачей некорректного сотояния окна приложения при его
        максимизации. Суть в том, что ComponentListner при максимизации фрейма получают последовательно два события,
        первое на перемещение окна, второе на изменение размера. В первом событии передается НЕПРАВИЛЬНЫЙ статус окна -
        "нормальный", хотя, судя по координатам окна, оно уже максимизировано. Следующее событие на изменение размера
        содержит уже корректный флаг.

        Вследствие данной ошибки, невозможно нормальным образом отследить и координаты немаксимизированного окна, для их
        сохранения и восстановления при перезапуске программы, так как, закрыв окно максимизированным, мы сохраняем
        неверные координаты нормального состояния окна.

        Данный патч призван обойти эту проблему. На входе он получает, текущие отслеживаемые координаты окна в
        нормальном состоянии, новые координаты окна и новое состояние окна, на выходе - новые корректные (кроме
        промежуточного состояния при максимизации, когда уже было событие на перемещение, но не было на изменение
        размера) координаты для окна в нормальном состоянии.
    */
    private class SwingMaxWindowPatch {
        private final Rectangle storedNormalStateBounds = new Rectangle();

        private boolean awaitMaximizing;

        public Rectangle GetNormalBoundsAfterChangeLocationEvent(Rectangle currentNormalStateBounds) {
            Rectangle newBounds = new Rectangle(currentNormalStateBounds);

            awaitMaximizing = isNormalFrameState() && (mainFrame.getLocation().x == -4)
                    && (mainFrame.getLocation().y == -4);

            if (awaitMaximizing) {
                storedNormalStateBounds.setBounds(currentNormalStateBounds);
            }
            
            if (isNormalFrameState()) {
                newBounds.setLocation(mainFrame.getLocation());
            }

            return newBounds;
        }

        public Rectangle GetNormalBoundsAfterChangeSizeEvent(Rectangle currentNormalStateBounds) {
            Rectangle newBounds = new Rectangle(currentNormalStateBounds);

            if (isNormalFrameState()) {
                newBounds.setSize(mainFrame.getSize());
            }

            if (isFrameMaximized() && awaitMaximizing) {
                newBounds.setLocation(storedNormalStateBounds.getLocation());

                awaitMaximizing = false;
            }

            return newBounds;
        }
    }
}
