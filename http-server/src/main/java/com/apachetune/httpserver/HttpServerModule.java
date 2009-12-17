package com.apachetune.httpserver;

import com.apachetune.core.*;
import static com.apachetune.httpserver.Constants.*;
import com.apachetune.httpserver.impl.*;
import com.apachetune.httpserver.ui.*;
import com.apachetune.httpserver.ui.resources.*;
import com.google.inject.*;
import static com.google.inject.Scopes.*;
import com.google.inject.name.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
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
