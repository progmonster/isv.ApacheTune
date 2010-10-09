package com.apachetune.httpserver.impl;

import com.apachetune.core.preferences.PreferencesManager;
import org.jmock.Mockery;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.fail;

/**
 * FIXDOC
 */
@Test
public class RecentOpenedServersManagerImplTest {
    @Test
    public void testStoreServerUriToRecentList() throws Exception {
/*
        Mockery mockCtx = new Mockery();

        PreferencesManager mockPreferencesManager = mockCtx.mock(PreferencesManager.class);

        RecentOpenedServersManagerImpl subject = new RecentOpenedServersManagerImpl(mockPreferencesManager);

        subject.storeServerUriToRecentList(new URI("http://localhost/fake_server_path"));
*/

      //  mockCtx.assertIsSatisfied();
    }

    @Test
    public void testGetLastOpenedServerUri() throws Exception {
        //
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
