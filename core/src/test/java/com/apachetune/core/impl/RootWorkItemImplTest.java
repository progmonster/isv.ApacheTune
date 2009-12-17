package com.apachetune.core.impl;

import com.apachetune.core.*;
import org.jmock.*;
import org.testng.annotations.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class RootWorkItemImplTest {
    @Test
    public void testRaiseEventsOnChildActivation() {
        Mockery mockContext = new Mockery();

        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        final WorkItem a = new SimpleWorkItem("A_WORK_ITEM");

        rootWorkItem.addChildWorkItem(a);

        final WorkItem aa = new SimpleWorkItem("AA_WORK_ITEM");

        a.addChildWorkItem(aa);

        final ActivationListener mockChildActivationListener = mockContext.mock(ActivationListener.class);

        rootWorkItem.addChildActivationListener(mockChildActivationListener);

        final Sequence expSequence = mockContext.sequence("expSequence");

        mockContext.checking(new Expectations() {{
            oneOf(mockChildActivationListener).onActivate(a); inSequence(expSequence);
            oneOf(mockChildActivationListener).onActivate(aa); inSequence(expSequence);
        }});

        aa.activate();

        mockContext.assertIsSatisfied();
    }

    @Test
    public void testRaiseEventsOnChildDeactivation() {
        Mockery mockContext = new Mockery();

        RootWorkItem rootWorkItem = new RootWorkItemImpl();

        final WorkItem a = new SimpleWorkItem("A_WORK_ITEM");

        rootWorkItem.addChildWorkItem(a);

        final WorkItem aa = new SimpleWorkItem("AA_WORK_ITEM");

        a.addChildWorkItem(aa);

        aa.activate();

        final ActivationListener mockChildActivationListener = mockContext.mock(ActivationListener.class);

        rootWorkItem.addChildActivationListener(mockChildActivationListener);

        final Sequence expSequence = mockContext.sequence("expSequence");

        mockContext.checking(new Expectations() {{
            oneOf(mockChildActivationListener).onDeactivate(aa); inSequence(expSequence);
            oneOf(mockChildActivationListener).onDeactivate(a); inSequence(expSequence);
        }});

        rootWorkItem.deactivate();

        mockContext.assertIsSatisfied();
    }
}

