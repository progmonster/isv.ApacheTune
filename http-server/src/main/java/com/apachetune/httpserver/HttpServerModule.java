package com.apachetune.httpserver;

import com.apachetune.httpserver.impl.HttpServerManagerImpl;
import com.apachetune.httpserver.impl.RecentOpenedServersManagerImpl;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.impl.HttpServerWorkItemImpl;
import com.apachetune.httpserver.ui.messagesystem.MessageManager;
import com.apachetune.httpserver.ui.messagesystem.impl.MessageManagerImpl;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.AbstractModule;

import static com.google.inject.Scopes.SINGLETON;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class HttpServerModule extends AbstractModule {
    protected void configure() {
        bind(HttpServerWorkItem.class).to(HttpServerWorkItemImpl.class).in(SINGLETON);

        bind(HttpServerResourceLocator.class).in(SINGLETON);

        bind(HttpServerManager.class).to(HttpServerManagerImpl.class).in(SINGLETON);

        bind(RecentOpenedServersManager.class).to(RecentOpenedServersManagerImpl.class).in(SINGLETON);

        bind(MessageManager.class).to(MessageManagerImpl.class).in(SINGLETON);
    }
}
