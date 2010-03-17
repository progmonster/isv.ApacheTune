package com.apachetune.httpserver;

import com.apachetune.core.ModuleController;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.WorkItem;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static com.apachetune.httpserver.Constants.HTTP_SERVER_WORK_ITEM;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpServerModuleController implements ModuleController {
    private final Module httpServerModule = new HttpServerModule();

    @Inject @Named(CORE_UI_WORK_ITEM)
    private WorkItem coreUIWorkItem;

    @Inject @Named(HTTP_SERVER_WORK_ITEM)
    private WorkItem httpServerWorkItem;


    public Module getModule() {
        return httpServerModule;
    }

    public void initialize(RootWorkItem rootWorkItem) {
        if (rootWorkItem == null) {
            throw new NullPointerException("Argument rootWorkItem cannot be a null [this = " + this + "]");
        }

        coreUIWorkItem.addChildWorkItem(httpServerWorkItem);
    }
}
