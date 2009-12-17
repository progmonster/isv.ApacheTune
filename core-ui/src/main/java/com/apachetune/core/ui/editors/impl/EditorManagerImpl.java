package com.apachetune.core.ui.editors.impl;

import com.apachetune.core.ui.editors.*;
import com.apachetune.core.ui.actions.*;
import static com.apachetune.core.ui.Constants.FILE_SAVE_ALL_ACTION;
import com.apachetune.core.*;
import com.google.inject.*;

import java.beans.*;
import java.util.*;

import org.apache.commons.collections.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class EditorManagerImpl implements EditorManager, WorkItemLifecycleListener, PropertyChangeListener,
        SaveAllFilesActionSite {
    private final ActionManager actionManager;

    private final Provider<EditorWorkItem> editorWorkItemProvider;

    private final Set<EditorWorkItem> editorWorkItems = new HashSet<EditorWorkItem>();

    private boolean saveAllFilesEnabled;

    @Inject
    public EditorManagerImpl(Provider<EditorWorkItem> editorWorkItemProvider, ActionManager actionManager) {
        this.editorWorkItemProvider = editorWorkItemProvider;
        this.actionManager = actionManager;

        actionManager.activateActionSites(this);
    }

    public EditorWorkItem createEditorWorkItem(EditorInput editorInput) {
        if (editorInput == null) {
            throw new NullPointerException("Argument editorInput cannot be a null [this = " + this + "]");
        }
        
        EditorWorkItem editorWorkItem = editorWorkItemProvider.get();

        editorWorkItem.setEditorInput(editorInput);

        editorWorkItems.add(editorWorkItem);

        editorWorkItem.addLifecycleListener(this);
        
        return editorWorkItem;
    }

    public void saveAllEditorsContent() {
        if (!isSaveAllFilesEnabled()) {
            return;
        }

        for (EditorWorkItem editorWorkItem : editorWorkItems) {
            if (editorWorkItem.isDirty()) {
                editorWorkItem.save();
            }
        }
    }

    public void onInitialized(WorkItem workItem) {
        workItem.addPropertyChangeListener(this);
    }

    public void onDisposed(WorkItem workItem) {
        workItem.removePropertyChangeListener(this);
        workItem.removeLifecycleListener(this);

        //noinspection SuspiciousMethodCalls
        editorWorkItems.remove(workItem);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ((evt.getSource() instanceof EditorWorkItem) && evt.getPropertyName().equals("dirty")) {
            if (evt.getNewValue() instanceof Boolean) {
                Boolean newValue = (Boolean) evt.getNewValue();

                if (newValue) {
                    setSaveAllFilesEnabled(true);
                } else {
                    boolean isDirtyEditorExists = CollectionUtils.exists(editorWorkItems, new Predicate() {
                        public boolean evaluate(Object object) {
                            EditorWorkItem editorWorkItem = (EditorWorkItem) object;

                            return editorWorkItem.isDirty();
                        }
                    });                            

                    setSaveAllFilesEnabled(isDirtyEditorExists);
                }
            }
        }
    }

    @ActionHandler(FILE_SAVE_ALL_ACTION)
    public void onSaveAllFiles() {
        for (EditorWorkItem editorWorkItem : editorWorkItems) {
            if (editorWorkItem.isDirty()) {
                editorWorkItem.save();
            }
        }

        setSaveAllFilesEnabled(false);
    }

    @ActionPermission(FILE_SAVE_ALL_ACTION)
    public boolean isSaveAllFilesEnabled() {
        return saveAllFilesEnabled;
    }

    private void setSaveAllFilesEnabled(boolean saveAllFilesEnabled) {
        this.saveAllFilesEnabled = saveAllFilesEnabled;
        
        actionManager.updateActionSites(this);
    }
}
