package com.apachetune.core.ui;

import com.apachetune.core.*;
import static com.apachetune.core.ui.Constants.CORE_UI_WORK_ITEM;
import com.google.inject.*;
import com.google.inject.name.*;

import java.awt.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class CoreUIModuleController implements ModuleController {
    private final Module coreUIModule = new CoreUIModule();

    @Inject @Named(CORE_UI_WORK_ITEM)
    private WorkItem coreUIWorkItem;

    public void initialize(RootWorkItem rootWorkItem) {
        if (rootWorkItem == null) {
            throw new NullPointerException("Argument rootWorkItem cannot be a null [this = " + this + "]");
        }

        rootWorkItem.addChildWorkItem(coreUIWorkItem);

        setEDTLogger();
    }

    public Module getModule() {
        return coreUIModule;
    }

    private void setEDTLogger() {
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

        eventQueue.push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                try {
                    super.dispatchEvent(event);
                } catch (Throwable cause) {
                    // TODO FIX. Exceptions do not catch by with section.
                    cause.printStackTrace(); // TODO log it. And send feedback.
                }
            }
        });
    }
}
