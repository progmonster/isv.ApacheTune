package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.*;
import org.jmock.*;
import org.testng.annotations.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class StatusBarManagerImplTest {
    public void testAddMainStatus() {
        Mockery mockContext = new Mockery();

        final StatusBarView mockStatusBarView = mockContext.mock(StatusBarView.class);

        StatusBarManager statusBarManagerImpl = new StatusBarManagerImpl(mockStatusBarView);

        final Sequence sequence = mockContext.sequence("flow");

        mockContext.checking(new Expectations() {{
            one(mockStatusBarView).initialize(); inSequence(sequence);    

            one(mockStatusBarView).setMainMessage("message1"); inSequence(sequence);    
            one(mockStatusBarView).setMainMessage("message2"); inSequence(sequence);
            one(mockStatusBarView).setMainMessage("message3"); inSequence(sequence);    
            one(mockStatusBarView).setMainMessage("message4"); inSequence(sequence);
        }});

        statusBarManagerImpl.initialize();

        statusBarManagerImpl.addMainStatus("id1", "message1");
        statusBarManagerImpl.addMainStatus("id2", "message2");
        statusBarManagerImpl.addMainStatus("id2", "message3");
        statusBarManagerImpl.addMainStatus("id1", "message4");

        mockContext.assertIsSatisfied();
    }

    @Test
    public void testRemoveMainStatus() {
        Mockery mockContext = new Mockery();

        final StatusBarView mockStatusBarView = mockContext.mock(StatusBarView.class);

        StatusBarManager statusBarManager = new StatusBarManagerImpl(mockStatusBarView);

        final Sequence sequence = mockContext.sequence("flow");

        mockContext.checking(new Expectations() {{
            one(mockStatusBarView).initialize(); inSequence(sequence);

            one(mockStatusBarView).setMainMessage("message1"); inSequence(sequence);
            one(mockStatusBarView).setMainMessage("message2"); inSequence(sequence);
            one(mockStatusBarView).setMainMessage("message3"); inSequence(sequence);    

            allowing(mockStatusBarView).setMainMessage("message3"); inSequence(sequence);

            one(mockStatusBarView).setMainMessage(""); inSequence(sequence);    
        }});

        statusBarManager.initialize();

        statusBarManager.addMainStatus("id1", "message1");
        statusBarManager.addMainStatus("id1", "message2");
        statusBarManager.addMainStatus("id2", "message3");
        
        statusBarManager.removeMainStatus("id1");
        statusBarManager.removeMainStatus("id1");
        statusBarManager.removeMainStatus("id2");

        mockContext.assertIsSatisfied();
    }
}
