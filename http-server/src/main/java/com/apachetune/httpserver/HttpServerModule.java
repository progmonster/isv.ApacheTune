package com.apachetune.httpserver;

import com.apachetune.core.WorkItem;
import com.apachetune.httpserver.impl.HttpServerManagerImpl;
import com.apachetune.httpserver.impl.RecentOpenedServersManagerImpl;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import static com.apachetune.httpserver.Constants.HTTP_SERVER_WORK_ITEM;
import static com.google.inject.Scopes.SINGLETON;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpServerModule extends AbstractModule {
    protected void configure() {
        bind(WorkItem.class).annotatedWith(Names.named(HTTP_SERVER_WORK_ITEM)).to(HttpServerWorkItem.class).in(
                SINGLETON);

        bind(HttpServerResourceLocator.class).in(SINGLETON);

        bind(HttpServerManager.class).to(HttpServerManagerImpl.class).in(SINGLETON);

        bind(RecentOpenedServersManager.class).to(RecentOpenedServersManagerImpl.class).in(SINGLETON);
    }
}
