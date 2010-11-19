package com.apachetune.core.impl;

import com.apachetune.core.*;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class RootWorkItemImplTest extends WorkItemAbstractTest {
    @Test
    public void testRaiseEventsOnChildActivation() {
        final WorkItem a = new SimpleWorkItem("A_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(a);

        final WorkItem aa = new SimpleWorkItem("AA_WORK_ITEM");

        a.addChildWorkItem(aa);

        final ActivationListener mockChildActivationListener = getMockContext().mock(ActivationListener.class);

        getRootWorkItem().addChildActivationListener(mockChildActivationListener);

        final Sequence expSequence = getMockContext().sequence("expSequence");

        getMockContext().checking(new Expectations() {{
            oneOf(mockChildActivationListener).onActivate(a); inSequence(expSequence);
            oneOf(mockChildActivationListener).onActivate(aa); inSequence(expSequence);
        }});

        aa.activate();

        getMockContext().assertIsSatisfied();
    }

    @Test
    public void testRaiseEventsOnChildDeactivation() {
        final WorkItem a = new SimpleWorkItem("A_WORK_ITEM");

        getRootWorkItem().addChildWorkItem(a);

        final WorkItem aa = new SimpleWorkItem("AA_WORK_ITEM");

        a.addChildWorkItem(aa);

        aa.activate();

        final ActivationListener mockChildActivationListener = getMockContext().mock(ActivationListener.class);

        getRootWorkItem().addChildActivationListener(mockChildActivationListener);

        final Sequence expSequence = getMockContext().sequence("expSequence");

        getMockContext().checking(new Expectations() {{
            oneOf(mockChildActivationListener).onDeactivate(aa); inSequence(expSequence);
            oneOf(mockChildActivationListener).onDeactivate(a); inSequence(expSequence);
        }});

        getRootWorkItem().deactivate();

        getMockContext().assertIsSatisfied();
    }
}

