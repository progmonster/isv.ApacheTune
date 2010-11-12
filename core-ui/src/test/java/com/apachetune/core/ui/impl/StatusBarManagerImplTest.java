package com.apachetune.core.ui.impl;

import com.apachetune.core.ui.statusbar.StatusBarManager;
import com.apachetune.core.ui.statusbar.StatusBarView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Test;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class StatusBarManagerImplTest {
    @Test
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
