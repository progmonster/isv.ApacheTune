package com.apachetune.main;

import com.apachetune.core.ModuleController;
import com.apachetune.core.RootWorkItem;
import com.google.inject.Inject;
import com.google.inject.Module;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class MainModuleController implements ModuleController {
    private final MainModule mainModule = new MainModule();

    @Inject
    private RootWorkItem rootWorkItem;

    public Module getModule() {
        return mainModule;
    }

    public void initialize(RootWorkItem rootWorkItem) {
        isTrue(rootWorkItem == null, "Argument rootWorkItem should be a null because main module controller" +
                    "creates a rootWorkItem oneself [rootWorkItem = " + rootWorkItem + "; this = " + this +"]");
    }

    public RootWorkItem getRootWorkItem() {
        return rootWorkItem;
    }
}
