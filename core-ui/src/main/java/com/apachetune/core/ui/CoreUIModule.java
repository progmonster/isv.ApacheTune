package com.apachetune.core.ui;

import com.apachetune.core.*;
import static com.apachetune.core.ui.Constants.*;
import com.apachetune.core.ui.actions.*;
import com.apachetune.core.ui.actions.impl.*;
import com.apachetune.core.ui.editors.*;
import com.apachetune.core.ui.editors.impl.*;
import com.apachetune.core.ui.impl.*;
import com.apachetune.core.ui.resources.*;
import com.google.inject.*;
import static com.google.inject.Scopes.*;
import static com.google.inject.name.Names.*;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.*;

import javax.swing.*;

/**
 * FIXDOC                                   
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CoreUIModule extends AbstractModule {
    protected void configure() {
        bind(WorkItem.class).annotatedWith(named(CORE_UI_WORK_ITEM)).to(CoreUIWorkItem.class).in(SINGLETON);

        bind(JFrame.class).in(SINGLETON);

        bind(ToolWindowManager.class).annotatedWith(named(TOOL_WINDOW_MANAGER)).to(MyDoggyToolWindowManager.class)
                .in(SINGLETON);

        bind(MenuBarManager.class).to(MenuBarManagerImpl.class).in(SINGLETON);

        bind(ToolBarManager.class).to(ToolBarManagerImpl.class).in(SINGLETON);

        bind(StatusBarManager.class).to(StatusBarManagerImpl.class).in(SINGLETON);

        bind(ActionManager.class).to(ActionManagerImpl.class).in(SINGLETON);

        bind(CoreUIResourceLocator.class).in(SINGLETON);

        bind(CoreUIUtils.class).in(SINGLETON);

        bind(JToolBar.class).in(SINGLETON);

        bind(StatusBarManagerImpl.class).in(SINGLETON);

        bind(StatusBarView.class).to(StatusBarViewImpl.class).in(SINGLETON);

        bind(ToolBarManagerImpl.class).in(SINGLETON);

        bind(TitleBarManager.class).to(TitleBarManagerImpl.class).in(SINGLETON);

        bind(EditorManager.class).to(EditorManagerImpl.class).in(SINGLETON);

        bind(EditorWorkItem.class).to(EditorWorkItemImpl.class);

        bind(SaveFilesHelper.class).annotatedWith(named(SAVE_ALL_FILES_AT_ONCE_HELPER)).to(SaveAllFilesAtOnceHelperImpl
                .class);

        bind(SaveFilesHelper.class).annotatedWith(named(SAVE_ALL_FILES_SEPARATELY_HELPER)).to(
                SaveFilesSeparatelyHelperImpl.class);
        
        bind(OutputPaneDocument.class).to(OutputPaneDocumentImpl.class).in(SINGLETON);
    }
}
