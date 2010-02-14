package com.apachetune.core.ui.editors.impl;

import com.apachetune.core.preferences.Preferences;
import com.apachetune.core.preferences.*;
import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.editors.*;
import com.google.inject.*;
import com.google.inject.name.*;
import jsyntaxpane.*;
import org.noos.xing.mydoggy.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import static java.awt.Color.RED;
import java.awt.event.*;
import java.awt.print.*;
import static java.lang.Math.*;
import java.net.*;
import java.text.*;
import java.util.prefs.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class EditorWorkItemImpl extends GenericUIWorkItem implements EditorWorkItem, EditorActionSite,
        SaveFileActionSite, UndoWorkflowActionSite, PrintDocumentActionSite  {
    private static final char EDITOR_DIRTY_FLAG = '*';

    private static final Color HIGHLIGHT_ERROR_LINE_COLOR = RED;

    private final ToolWindowManager toolWindowManager;

    private final ActionManager actionManager;

    private final StatusBarManager statusBarManager;

    private final MenuBarManager menuBarManager;

    private final PreferencesManager preferencesManager;

    private final CoreUIUtils coreUIUtils;

    private EditorInput editorInput;

    private Content contentPane;

    private JEditorPane editorPane;

    private final EditorPaneFocusListener editorPaneFocusListener = new EditorPaneFocusListener();

    private boolean dirty;

    public boolean canUndo;

    public boolean canRedo;

    @Inject
    public EditorWorkItemImpl(@Named(NESTED_TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            ActionManager actionManager, StatusBarManager statusBarManager, MenuBarManager menuBarManager,
            PreferencesManager preferencesManager, CoreUIUtils coreUIUtils) {
        this.toolWindowManager = toolWindowManager;
        this.actionManager = actionManager;
        this.statusBarManager = statusBarManager;
        this.menuBarManager = menuBarManager;
        this.preferencesManager = preferencesManager;
        this.coreUIUtils = coreUIUtils;
    }

    public void setEditorInput(EditorInput editorInput) {
        if (editorInput == null) {
            throw new NullPointerException("Argument editorInput cannot be a null [this = " + this + "]");
        }

        this.editorInput = editorInput;

        setId(editorInput.getWorkItemId());
    }

    @ActionHandler(EDIT_COPY_ACTION)
    public void onCopy() {
        editorPane.copy();
    }

    @ActionPermission(EDIT_COPY_ACTION)
    public boolean isCopyEnabled() {
        return true;
    }

    @ActionHandler(EDIT_CUT_ACTION)
    public void onCut() {
        editorPane.cut();
    }

    @ActionPermission(EDIT_CUT_ACTION)
    public boolean isCutEnabled() {
        return true;
    }

    @ActionHandler(EDIT_PASTE_ACTION)
    public void onPaste() {
        editorPane.paste();
    }

    @ActionPermission(EDIT_PASTE_ACTION)
    public boolean isPasteEnabled() {
        return true;
    }

    @ActionHandler(EDIT_SELECT_ALL_ACTION)
    public void onSelectAll() {
        editorPane.grabFocus();

        editorPane.selectAll();
    }

    @ActionPermission(EDIT_SELECT_ALL_ACTION)
    public boolean isSelectAllEnabled() {
        return true;
    }

    @ActionHandler(FILE_SAVE_ACTION)
    public void onSaveFile() {
        save();
    }

    @ActionPermission(FILE_SAVE_ACTION)
    public boolean isSaveFileEnabled() {
        return dirty;
    }

    @ActionHandler(EDIT_UNDO_ACTION)
    public void onUndo() {
        getDocument().doUndo();

        updateUndoRedoActions();
    }

    @ActionPermission(EDIT_UNDO_ACTION)
    public boolean isUndoEnabled() {
        return canUndo;
    }

    @ActionHandler(EDIT_REDO_ACTION)
    public void onRedo() {
        getDocument().doRedo();

        updateUndoRedoActions();
    }

    @ActionPermission(EDIT_REDO_ACTION)
    public boolean isRedoEnabled() {
        return canRedo;
    }

    @ActionHandler(FILE_PRINT_ACTION)
    public void onPrintDocument() {
        /* TODO Rewrite printing with using manual creating pages. Need to intend a dual-printing ability, line-numbers,
         --TODO icons of a line-break and borders around a text to print.*/

        statusBarManager.addMainStatus(PRINT_DOCUMENT_STATUS, "Printing..."); // TODO Localize.

        try {
            String content = getContent();

            DefaultStyledDocument document = new DefaultStyledDocument();

            document.replace(0, document.getLength(), content, null);

            JTextPane textPane = new JTextPane(document);

            textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));

            String header = editorInput.getPrintTitle();

            textPane.print(new MessageFormat(header), new MessageFormat("Page {0}"), true, null, null, true);
        } catch (PrinterException e) {
            e.printStackTrace(); // TODO: User frendly message.
        } catch (BadLocationException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        } finally {
            statusBarManager.removeMainStatus(PRINT_DOCUMENT_STATUS);
        }
    }

    @ActionPermission(FILE_PRINT_ACTION)
    public boolean isPrintDocumentEnabled() {
        return true;
    }

    public void save() {
        if (!dirty) {
            return;
        }

        // TODO Localize.
        statusBarManager.addMainStatus(SAVE_FILE_STATUS, "Saving " + editorInput.getSaveTitle() + "...");

        try {
            editorInput.saveContent(getContent());

            setDirty(false);
        } finally {
            statusBarManager.removeMainStatus(SAVE_FILE_STATUS);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    protected void doUIInitialize() {
        checkEditorInput();

        createEditorPaneContent();

        editorPane.grabFocus();
    }

    protected void doUIDispose() {
        try {
            storeCaretPosition();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }

        editorPane.requestFocus();

        clearEditorPaneUndoManagerState();

        removeEditorPaneContent();

        statusBarManager.setCursorPositionState(null);
    }

    @Override
    protected void doActivation() {
        contentPane.setSelected(true);
        editorPane.grabFocus();
    }

    private void createEditorPaneContent() {
        editorPane = new JEditorPane();

        JScrollPane editorScrollPane = new JScrollPane(editorPane);

        editorPane.setContentType(editorInput.getContentType());
        editorPane.setText(editorInput.loadContent());

        clearEditorPaneUndoManagerState();

        initEditorPaneCaretListener(); // TODO #55, #24 (set last caret position for the document)
        restoreCaretPosition();
        initEditorPaneUndoRedoListener();
        initEditorPaneDocumentListener();

        contentPane = coreUIUtils.addContentToNestedToolWindowManager(editorInput.getContentPaneId(), editorInput
                .getContentPaneTitle(), editorInput.getContentPaneIcon(), editorScrollPane, editorInput
                        .getSaveTitle());

        contentPane.getContentUI().setCloseable(false);
        contentPane.getContentUI().setDetachable(false);
        contentPane.getContentUI().setMinimizable(false);

        editorPane.addFocusListener(editorPaneFocusListener);

        menuBarManager.createAndBindContextMenu(editorPane, this);
    }

    private void restoreCaretPosition() {
        int caretPosition = 0;

        if (hasStoredCaretPosition()) {
            caretPosition = getStoredCaretPosition();
        }

        // TODO It uses a document length instead of editorPane.getText().length() to avoid the "\r\n" problem.
        int contentLength = editorPane.getDocument().getLength();

        editorPane.setCaretPosition(min(caretPosition, contentLength));
    }

    private void initEditorPaneDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                setDirty(true);
            }

            public void removeUpdate(DocumentEvent e) {
                setDirty(true);
            }

            public void changedUpdate(DocumentEvent e) {
                setDirty(true);
            }
        });
    }

    private void initEditorPaneUndoRedoListener() {
        getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                updateUndoAndRedoActions(e.getEdit().canUndo(), e.getEdit().canRedo());
            }
        });
    }

    public ExtendedSyntaxDocument getDocument() {
        return (ExtendedSyntaxDocument) editorPane.getDocument();
    }

    public int getCaretPosition() {
        return editorPane.getCaretPosition();
    }

    public void setCaretPosition(int position) {
        editorPane.setCaretPosition(position);

        try {
            Rectangle caretRect = editorPane.modelToView(position);

            Rectangle scrollRect = new Rectangle(caretRect.x, caretRect.y, caretRect.width, editorPane.getVisibleRect()
                    .height);

            editorPane.scrollRectToVisible(scrollRect);
        } catch (BadLocationException e) {
            throw new RuntimeException("Internal error", e); // TODO Make it with a service.
        }
    }

    public int getLineStartPosition(int lineNum) {
        int lineCount = getDocument().getLineCount();

        if ((lineNum < 1) || (lineNum > lineCount)) {
            throw new IllegalArgumentException("Argument lineNum cannot be less than unity and greater than " +
                    lineCount + "[lineNum = " + lineNum + "; this = " + this + ']');
        }

        return getDocument().getDefaultRootElement().getElement(lineNum - 1).getStartOffset();
    }

    public void highlightLine(int lineNum, Color red) {
        int lineCount = getDocument().getLineCount();

        if ((lineNum < 1) || (lineNum > lineCount)) {
            throw new IllegalArgumentException("Argument lineNum cannot be less than unity and greater than " +
                    lineCount + "[lineNum = " + lineNum + "; this = " + this + ']');
        }

        Element lineElement = getDocument().getDefaultRootElement().getElement(lineNum - 1);

        editorPane.select(lineElement.getStartOffset(), lineElement.getEndOffset() - 1);

        new SelfReleasingErrorLineHighlighter(editorPane, lineElement.getStartOffset(), lineElement.getEndOffset() - 1);
    }

    private void updateUndoRedoActions() {
        UndoManager undoManager = getDocument().getUndoManager();

        updateUndoAndRedoActions(undoManager.canUndo(), undoManager.canRedo());
    }

    private void updateUndoAndRedoActions(boolean canUndo, boolean canRedo) {
        this.canUndo = canUndo;
        this.canRedo = canRedo;

        actionManager.updateActionSites(this);
    }

    private void removeEditorPaneContent() {
        Content editorPaneContent = toolWindowManager.getContentManager().getContent(getId());

        if (editorPaneContent != null) {
            toolWindowManager.getContentManager().removeContent(editorPaneContent);
        }

        editorPane.removeFocusListener(editorPaneFocusListener);

        editorPane = null;
    }

    private void initEditorPaneCaretListener() {
        editorPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                Element root = getDocument().getDefaultRootElement();

                int dot = e.getDot();

                int line = root.getElementIndex( dot );

                int col = dot - root.getElement( line ).getStartOffset();

                statusBarManager.setCursorPositionState(new Point(col + 1, line + 1));
            }
        });
    }

    private void clearEditorPaneUndoManagerState() {
        getDocument().clearUndos();

        updateUndoAndRedoActions(false, false);
    }

    private void checkEditorInput() {
        if (editorInput == null) {
            throw new NullPointerException("Argument editorInput cannot be a null [this = " + this + "]");
        }
    }

    private String getContent() {
        return editorPane.getText();
    }

    private void setDirty(boolean isDirty) {
        if (dirty == isDirty) {
            return;
        }

        Boolean oldValue = dirty;

        dirty = isDirty;

        contentPane.setTitle(editorInput.getContentPaneTitle() + (isDirty ? EDITOR_DIRTY_FLAG : ""));

        actionManager.updateActionSites(this);

        firePropertyChangeEvent("dirty", oldValue, isDirty);
    }

    private URI getDocumentUri() {
        return editorInput.getDocumentUri();
    }

    private void storeCaretPosition() throws BackingStoreException {
        Preferences caretPrefsNode = preferencesManager.userNodeForPackage(EditorWorkItemImpl.class).node(
                CARET_POSITION_PREFS_NODE_NAME);

        caretPrefsNode.putInt(getDocumentUri().toASCIIString(), getCaretPosition());
        caretPrefsNode.flush();
    }

    private boolean hasStoredCaretPosition() {
        int storedPos = getStoredCaretPosition(); 

        return (storedPos != -1);
    }

    private int getStoredCaretPosition() {
        return preferencesManager.userNodeForPackage(EditorWorkItemImpl.class).node(CARET_POSITION_PREFS_NODE_NAME)
                .getInt(getDocumentUri().toASCIIString(), -1);
    }

    private class EditorPaneFocusListener extends FocusAdapter {
        public void focusGained(FocusEvent e) {
            activate();
        }
    }

    private class SelfReleasingErrorLineHighlighter extends DefaultHighlighter.DefaultHighlightPainter implements
            CaretListener, MouseListener
    {
        private final JEditorPane editorPane;

        private final Object highlight;

        public SelfReleasingErrorLineHighlighter(JEditorPane editorPane, int startOffset, int endOffset) {
            super(HIGHLIGHT_ERROR_LINE_COLOR);

            this.editorPane = editorPane;

            try {
                highlight = editorPane.getHighlighter().addHighlight(startOffset, endOffset, this);
            } catch (BadLocationException e) {
                throw new RuntimeException("Internal error", e); // TODO Make it with a service.
            }

            editorPane.addCaretListener(this);
            editorPane.addMouseListener(this);
        }

        public void caretUpdate(CaretEvent e) {
            releaseHighlighting();
        }

        public void mouseClicked(MouseEvent e) {
            // No-op.
        }

        public void mousePressed(MouseEvent e) {
            releaseHighlighting();
        }

        public void mouseEntered(MouseEvent e) {
            // No-op.
        }

        public void mouseExited(MouseEvent e) {
            // No-op.
        }

        public void mouseReleased(MouseEvent e) {
            // No-op.
        }

        private void releaseHighlighting() {
            editorPane.getHighlighter().removeHighlight(highlight);

            editorPane.removeCaretListener(this);
            editorPane.removeMouseListener(this);
        }
    }
}
