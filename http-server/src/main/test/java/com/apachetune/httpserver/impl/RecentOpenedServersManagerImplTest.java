package com.apachetune.httpserver.impl;

import com.apachetune.core.AppManager;
import com.apachetune.core.preferences.PreferencesManager;
import com.apachetune.core.preferences.impl.PreferencesManagerImpl;
import com.apachetune.httpserver.RecentOpenedServerListChangedListener;
import com.apachetune.httpserver.RecentOpenedServersManager;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;


/**
 * FIXDOC
 */
public class RecentOpenedServersManagerImplTest {
    private AppManager mockAppManager;

    private RecentOpenedServersManager firstTestSubj;

    private RecentOpenedServersManager secondTestSubj;

    private URI fakeServerUri1;

    private URI fakeServerUri2;

    @Before
    public void setUp() throws Exception {
        Mockery mockCtx = new Mockery();

        mockAppManager = mockCtx.mock(AppManager.class);

        mockCtx.checking(new Expectations() {{
            allowing(mockAppManager).getFullAppName(); will(returnValue("com.apachetune.test_instance"));
        }});

        PreferencesManager preferencesManager = new PreferencesManagerImpl(mockAppManager);

        preferencesManager.userNodeForPackage(RecentOpenedServersManagerImpl.class).removeNode();

        firstTestSubj = new RecentOpenedServersManagerImpl(preferencesManager);

        secondTestSubj = new RecentOpenedServersManagerImpl(preferencesManager);

        fakeServerUri1 = new URI("http:/progmonster.com/uri/test_uri_1");

        fakeServerUri2 = new URI("http:/progmonster.com/uri/test_uri_2");
    }

    @Test
    public void testStoreServerUriToRecentList() throws Exception {
        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);

        assertThat(secondTestSubj.getLastOpenedServerUri()).isEqualTo(fakeServerUri1);
    }

    @Test
    public void testGetLastOpenedServerUri() throws Exception {
        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);

        assertThat(secondTestSubj.getLastOpenedServerUri()).isEqualTo(fakeServerUri1);
    }

    @Test
    public void testHasLastOpenedServer() throws Exception {
        assertThat(firstTestSubj.hasLastOpenedServer()).isFalse();

        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);

        assertThat(secondTestSubj.hasLastOpenedServer()).isTrue();
    }

    @Test
    public void testClearServerUriList() throws Exception {
        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);
        firstTestSubj.storeServerUriToRecentList(fakeServerUri2);

        firstTestSubj.clearServerUriList();

        assertThat(secondTestSubj.hasLastOpenedServer()).isFalse();
    }

    @Test
    public void testGetServerUriList() throws Exception {
        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);
        firstTestSubj.storeServerUriToRecentList(fakeServerUri2);

        List<URI> servers = secondTestSubj.getServerUriList();

        assertThat(servers).isEqualTo(asList(fakeServerUri2, fakeServerUri1));
    }

    @Test
    public void testAddServerListChangedListener() throws Exception {
        final MutableBoolean wasEventRaised = new MutableBoolean(false);

        firstTestSubj.addServerListChangedListener(new RecentOpenedServerListChangedListener() {
            @Override
            public void onRecentOpenedServerListChanged() {
                wasEventRaised.setValue(true);
            }
        });

        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);

        assertThat(wasEventRaised.isTrue()).isTrue();
    }

    @Test
    public void testRemoveServerListChangedListener() throws Exception {
        final MutableBoolean wasEventRaised = new MutableBoolean(false);

        RecentOpenedServerListChangedListener listChangedListener = new RecentOpenedServerListChangedListener() {
            @Override
            public void onRecentOpenedServerListChanged() {
                wasEventRaised.setValue(true);
            }
        };

        firstTestSubj.addServerListChangedListener(listChangedListener);
        firstTestSubj.removeServerListChangedListener(listChangedListener);

        firstTestSubj.storeServerUriToRecentList(fakeServerUri1);

        assertThat(wasEventRaised.isFalse()).isTrue();
    }
}
