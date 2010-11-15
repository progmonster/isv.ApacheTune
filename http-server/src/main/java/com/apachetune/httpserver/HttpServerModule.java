package com.apachetune.httpserver;

import com.apachetune.httpserver.impl.HttpServerManagerImpl;
import com.apachetune.httpserver.impl.RecentOpenedServersManagerImpl;
import com.apachetune.httpserver.ui.HttpServerWorkItem;
import com.apachetune.httpserver.ui.impl.HttpServerWorkItemImpl;
import com.apachetune.httpserver.ui.messagesystem.MessageManager;
import com.apachetune.httpserver.ui.messagesystem.MessageStatusBarSite;
import com.apachetune.httpserver.ui.messagesystem.MessageStore;
import com.apachetune.httpserver.ui.messagesystem.RemoteManager;
import com.apachetune.httpserver.ui.messagesystem.impl.LocalMessageStoreImpl;
import com.apachetune.httpserver.ui.messagesystem.impl.MessageManagerImpl;
import com.apachetune.httpserver.ui.messagesystem.impl.MessageStatusBarSiteImpl;
import com.apachetune.httpserver.ui.messagesystem.impl.RemoteManagerImpl;
import com.apachetune.httpserver.ui.resources.HttpServerResourceLocator;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import static com.apachetune.httpserver.Constants.MESSAGE_STORE_DB_URL_PROP_NAME;
import static com.apachetune.httpserver.Constants.REMOTE_MESSAGE_SERVICE_URL_PROP_NAME;
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

        bind(RemoteManager.class).to(RemoteManagerImpl.class).in(SINGLETON);

        bind(MessageStore.class).to(LocalMessageStoreImpl.class).in(SINGLETON);

        bind(String.class).annotatedWith(Names.named(MESSAGE_STORE_DB_URL_PROP_NAME))
                .toInstance("jdbc:h2:message_db;FILE_LOCK=SERIALIZED");

        bind(MessageStatusBarSite.class).to(MessageStatusBarSiteImpl.class).in(SINGLETON);

/*
        bind(String.class).annotatedWith(Names.named(REMOTE_MESSAGE_SERVICE_URL_PROP_NAME)) // todo configure via maven
        profiles
                .toInstance("http://apachetune.com/services/news");
*/
        bind(String.class).annotatedWith(Names.named(REMOTE_MESSAGE_SERVICE_URL_PROP_NAME))
                .toInstance("http://localhost:8080/apachetune-fake-news-message-service/services/news");
     }
}
