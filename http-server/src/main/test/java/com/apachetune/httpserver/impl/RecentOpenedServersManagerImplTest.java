package com.apachetune.httpserver.impl;

import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.httpserver.RecentOpenedServersManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.net.URI;

import static org.fest.assertions.Fail.fail;


/**
 * FIXDOC
 */
public class RecentOpenedServersManagerImplTest {
    @Test
    public void testStoreServerUriToRecentList() throws Exception {
        Mockery mockCtx = new Mockery();

        PreferencesManager mockPreferencesManager = mockCtx.mock(PreferencesManager.class);

        mockCtx.checking(new Expectations() {{

        }});

        RecentOpenedServersManager testSubj = new RecentOpenedServersManagerImpl(mockPreferencesManager);

        testSubj.storeServerUriToRecentList(new URI("http:/progmonster.com/uri/test_uri_1"));

        mockCtx.assertIsSatisfied();

        fail();
    }

    @Test
    public void testGetLastOpenedServerUri() throws Exception {
    }

    @Test
    public void testHasLastOpenedServer() throws Exception {
    }

    @Test
    public void testClearServerUriList() throws Exception {
    }

    @Test
    public void testGetServerUriList() throws Exception {
    }

    @Test
    public void testAddServerListChangedListener() throws Exception {
    }

    @Test
    public void testRemoveServerListChangedListener() throws Exception {
    }
}
