package com.apachetune.core.ui.editors.impl;

import com.apachetune.core.WorkItem;
import com.apachetune.core.WorkItemLifecycleListener;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.editors.EditorInput;
import com.apachetune.core.ui.editors.EditorManager;
import com.apachetune.core.ui.editors.EditorWorkItem;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import static com.apachetune.core.ui.Constants.*;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class EditorManagerImpl implements EditorManager, WorkItemLifecycleListener, PropertyChangeListener,
        SaveAllFilesActionSite, WindowActionSite {
    private final ActionManager actionManager;

    private final ToolWindowManager toolWindowManager;

    private final Provider<EditorWorkItem> editorWorkItemProvider;

    private final Set<EditorWorkItem> editorWorkItems = new HashSet<EditorWorkItem>();

    private boolean saveAllFilesEnabled;

    @Inject
    public EditorManagerImpl(@Named(TOOL_WINDOW_MANAGER) ToolWindowManager toolWindowManager,
            Provider<EditorWorkItem> editorWorkItemProvider, ActionManager actionManager) {
        this.toolWindowManager = toolWindowManager;
        this.editorWorkItemProvider = editorWorkItemProvider;
        this.actionManager = actionManager;

        actionManager.activateActionSites(this);
    }

    public EditorWorkItem createEditorWorkItem(EditorInput editorInput) {
        notNull(editorInput, "Argument editorInput cannot be a null");
        
        EditorWorkItem editorWorkItem = editorWorkItemProvider.get();

        editorWorkItem.setEditorInput(editorInput);

        editorWorkItems.add(editorWorkItem);

        editorWorkItem.addLifecycleListener(this);

        actionManager.updateActionSites(this);
        
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

        actionManager.updateActionSites(this);
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

    @ActionHandler(WINDOW_SELECT_NEXT_TAB_ACTION)
    public void onNextWindowTab() {
        Content content = toolWindowManager.getContentManager().getNextContent();
        if (content != null)
            content.setSelected(true);
    }

    @ActionPermission(WINDOW_SELECT_NEXT_TAB_ACTION)
    public boolean isNextWindowTabEnabled() {
        return isTabNavigationEnabled();
    }

    @ActionHandler(WINDOW_SELECT_PREVIOUS_TAB_ACTION)
    public void onPreviousWindowTab() {
        Content content = toolWindowManager.getContentManager().getPreviousContent();
        if (content != null)
            content.setSelected(true);
    }

    @ActionPermission(WINDOW_SELECT_PREVIOUS_TAB_ACTION)
    public boolean isPreviousWindowTabEnabled() {
        return isTabNavigationEnabled();
    }

    private boolean isTabNavigationEnabled() {
        return editorWorkItems.size() > 1;
    }

    private void setSaveAllFilesEnabled(boolean saveAllFilesEnabled) {
        this.saveAllFilesEnabled = saveAllFilesEnabled;

        actionManager.updateActionSites(this);
    }
}
