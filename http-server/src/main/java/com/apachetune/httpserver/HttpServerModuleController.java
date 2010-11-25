package com.apachetune.httpserver;

import com.apachetune.core.ModuleController;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.WorkItem;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;

import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import static org.apache.commons.lang.Validate.notNull;

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

    @Inject
    private HttpServerWorkItem httpServerWorkItem;


    public Module getModule() {
        return httpServerModule;
    }

    public void initialize(RootWorkItem rootWorkItem) {
        notNull(rootWorkItem, "Argument rootWorkItem cannot be a null [this = " + this + "]");

        coreUIWorkItem.addChildWorkItem(httpServerWorkItem);
    }
}
