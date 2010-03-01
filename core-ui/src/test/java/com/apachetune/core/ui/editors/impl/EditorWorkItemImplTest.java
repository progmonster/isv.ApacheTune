package com.apachetune.core.ui.editors.impl;

import org.testng.annotations.Test;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class EditorWorkItemImplTest {
/* FIX tests
    private ActionManager mockActionManager;

    private StatusBarManager mockStatusBarManager;

    private MenuBarManager mockMenuBarManager;

    private PreferencesManager mockPreferencesManager;

    private Preferences mockEditorPrefsNode;

    private Preferences mockCaretPositionPrefsNode;

    private MockEditorInput mockEditorInput;

    private Mockery mockContext;

    private CoreUIUtils coreUIUtils;

    private RootWorkItem rootWorkItem;

    private ToolWindowManager toolWindowManager;

    @BeforeMethod
    public void setUp() {
        DefaultSyntaxKit.registerContentType("text/plain", ExtendedSyntaxKit.class.getName());

        mockContext = new Mockery();

        toolWindowManager = new MyDoggyToolWindowManager();

        mockActionManager = mockContext.mock(ActionManager.class);

        mockStatusBarManager = mockContext.mock(StatusBarManager.class);

        mockMenuBarManager = mockContext.mock(MenuBarManager.class);

        mockPreferencesManager = mockContext.mock(PreferencesManager.class);

        mockEditorPrefsNode = mockContext.mock(Preferences.class, "mockEditorPrefsNode");

        mockCaretPositionPrefsNode = mockContext.mock(Preferences.class, "mockCaretPositionPrefsNode");

        coreUIUtils = new CoreUIUtils(mockStatusBarManager, mockActionManager, toolWindowManager);

        rootWorkItem = new RootWorkItemImpl();

        mockEditorInput = new MockEditorInput();
    }

    @Test
    public void testSetCursorToTopLeftCornerForFirstOpenedDocument() {
        mockContext.checking(new Expectations() {{
            allowing(mockActionManager).updateActionSites(with(any(Object.class)));

            allowing(mockMenuBarManager).createAndBindContextMenu(with(any(Component.class)), with(any(ActionSite
                    .class)));

            allowing(mockStatusBarManager).setCursorPositionState(with(any(Point.class)));

            allowing(mockPreferencesManager).userNodeForPackage(EditorWorkItemImpl.class); will(returnValue(
                    mockEditorPrefsNode));

            allowing(mockEditorPrefsNode).node(CARET_POSITION_PREFS_NODE_NAME); will(returnValue(
                    mockCaretPositionPrefsNode));

            allowing(mockCaretPositionPrefsNode).getInt(mockEditorInput.getDocumentUri().toASCIIString(), -1); will(
                    returnValue(-1));

            allowing(mockEditorPrefsNode).node(VIEW_POSITION_PREFS_NODE_NAME); will(returnValue(0));
        }});

        EditorWorkItem editorWorkItem = new EditorWorkItemImpl(toolWindowManager, mockActionManager,
                mockStatusBarManager, mockMenuBarManager, mockPreferencesManager, coreUIUtils);

        editorWorkItem.setEditorInput(mockEditorInput);

        rootWorkItem.addChildWorkItem(editorWorkItem);

        editorWorkItem.initialize();

        mockContext.assertIsSatisfied();

        assertEquals(editorWorkItem.getCaretPosition(), 0);
    }

    @Test
    public void testRestoreCursorPositionForReopenedDocument() {
        mockContext.checking(new Expectations() {{
            allowing(mockActionManager).updateActionSites(with(any(Object.class)));

            allowing(mockMenuBarManager).createAndBindContextMenu(with(any(Component.class)), with(any(ActionSite
                    .class)));

            allowing(mockStatusBarManager).setCursorPositionState(with(any(Point.class)));

            allowing(mockPreferencesManager).userNodeForPackage(EditorWorkItemImpl.class); will(returnValue(
                    mockEditorPrefsNode));

            allowing(mockEditorPrefsNode).node(CARET_POSITION_PREFS_NODE_NAME); will(returnValue(
                    mockCaretPositionPrefsNode));

            allowing(mockCaretPositionPrefsNode).getInt(mockEditorInput.getDocumentUri().toASCIIString(), -1); will(
                    returnValue(10));
        }});
        
        EditorWorkItem editorWorkItem = new EditorWorkItemImpl(toolWindowManager, mockActionManager,
                mockStatusBarManager, mockMenuBarManager, mockPreferencesManager, coreUIUtils);

        editorWorkItem.setEditorInput(mockEditorInput);

        rootWorkItem.addChildWorkItem(editorWorkItem);

        editorWorkItem.initialize();

        mockContext.assertIsSatisfied();

        assertEquals(editorWorkItem.getCaretPosition(), 10);
    }


    @Test
    public void testSetCursorToLastPositionForTruncatedDocumentAndOutOfTheNewBoundsCursor() {
        mockContext.checking(new Expectations() {{
            allowing(mockActionManager).updateActionSites(with(any(Object.class)));

            allowing(mockMenuBarManager).createAndBindContextMenu(with(any(Component.class)), with(any(ActionSite
                    .class)));

            allowing(mockStatusBarManager).setCursorPositionState(with(any(Point.class)));

            allowing(mockPreferencesManager).userNodeForPackage(EditorWorkItemImpl.class); will(returnValue(
                    mockEditorPrefsNode));

            allowing(mockEditorPrefsNode).node(CARET_POSITION_PREFS_NODE_NAME); will(returnValue(
                    mockCaretPositionPrefsNode));

            allowing(mockCaretPositionPrefsNode).getInt(mockEditorInput.getDocumentUri().toASCIIString(), -1); will(
                    returnValue(100));
        }});

        EditorWorkItem editorWorkItem = new EditorWorkItemImpl(toolWindowManager, mockActionManager,
                mockStatusBarManager, mockMenuBarManager, mockPreferencesManager, coreUIUtils);

        editorWorkItem.setEditorInput(mockEditorInput);

        rootWorkItem.addChildWorkItem(editorWorkItem);

        editorWorkItem.initialize();

        mockContext.assertIsSatisfied();

        assertEquals(editorWorkItem.getCaretPosition(), mockEditorInput.loadContent().length() - 1);
    }

    @Test
    public void testStoreCursorPosition() throws Exception {
        mockContext.checking(new Expectations() {{
            allowing(mockActionManager).updateActionSites(with(any(Object.class)));

            allowing(mockMenuBarManager).createAndBindContextMenu(with(any(Component.class)), with(any(ActionSite
                    .class)));

            allowing(mockStatusBarManager).setCursorPositionState(with(any(Point.class)));

            allowing(mockPreferencesManager).userNodeForPackage(EditorWorkItemImpl.class); will(returnValue(
                    mockEditorPrefsNode));

            allowing(mockEditorPrefsNode).node(CARET_POSITION_PREFS_NODE_NAME); will(returnValue(
                    mockCaretPositionPrefsNode));

            allowing(mockCaretPositionPrefsNode).getInt(mockEditorInput.getDocumentUri().toASCIIString(), -1); will(
                    returnValue(-1));

            atLeast(1).of(mockCaretPositionPrefsNode).putInt(mockEditorInput.getDocumentUri().toASCIIString(), 5);
            atLeast(1).of(mockCaretPositionPrefsNode).flush();
        }});

        EditorWorkItem editorWorkItem = new EditorWorkItemImpl(toolWindowManager, mockActionManager,
                mockStatusBarManager, mockMenuBarManager, mockPreferencesManager, coreUIUtils);

        editorWorkItem.setEditorInput(mockEditorInput);

        rootWorkItem.addChildWorkItem(editorWorkItem);

        editorWorkItem.initialize();
        editorWorkItem.setCaretPosition(5);
        editorWorkItem.dispose();

        mockContext.assertIsSatisfied();
    }
}

class MockEditorInput implements EditorInput {
    public String getWorkItemId() {
        return "TEST_EDITOR_WORK_ITEM";
    }

    public String getContentPaneTitle() {
        return "TEST_EDITOR";
    }

    public String getContentPaneId() {
        return "TEST_EDITOR_PANE";
    }

    public String getPrintTitle() {
        return "TEST_EDITOR";
    }

    public String getSaveTitle() {
        return "TEST_EDITOR";
    }

    public String getContentType() {
        return "text/plain";
    }

    public void saveContent(String content) {
        // No-op.
    }

    public Icon getContentPaneIcon() {
        return null;
    }

    public String loadContent() {
        return "Any text\nAnother line.";
    }

    public URI getDocumentUri() {
        try {
            return new URI("WORK_ITEM");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Internal error.", e);
        }
    }
*/
}
