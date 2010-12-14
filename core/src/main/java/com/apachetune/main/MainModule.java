package com.apachetune.main;

import com.apachetune.core.AppManager;
import com.apachetune.core.LicenseManager;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.impl.AppManagerImpl;
import com.apachetune.core.impl.LicenseManagerImpl;
import com.apachetune.core.impl.RootWorkItemImpl;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.impl.PreferencesManagerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import static com.apachetune.core.Constants.REMOTE_FEEDBACK_SERVICE_URL_PROP;
import static com.apachetune.core.utils.Utils.createRuntimeException;
import static com.google.inject.Scopes.SINGLETON;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class MainModule extends AbstractModule {
    private final Scheduler scheduler;

    public MainModule() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw createRuntimeException("Cannot create scheduler.", e);
        }
    }

    protected void configure() {
        bind(RootWorkItem.class).to(RootWorkItemImpl.class).in(SINGLETON);

        bind(AppManager.class).to(AppManagerImpl.class).in(SINGLETON);

        bind(LicenseManager.class).to(LicenseManagerImpl.class).in(SINGLETON);

        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(SINGLETON);

        bind(Scheduler.class).toInstance(scheduler);

        bind(String.class).annotatedWith(Names.named(REMOTE_FEEDBACK_SERVICE_URL_PROP))
                .toInstance("http://apachetune.com/services/reports");
    }
}
