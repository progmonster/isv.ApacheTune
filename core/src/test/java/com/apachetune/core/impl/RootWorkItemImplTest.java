package com.apachetune.core.impl;

import com.apachetune.core.ActivationListener;
import com.apachetune.core.RootWorkItem;
import com.apachetune.core.SimpleWorkItem;
import com.apachetune.core.WorkItem;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.testng.annotations.Test;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
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

