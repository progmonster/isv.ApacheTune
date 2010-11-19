package com.apachetune.core;

import com.apachetune.core.utils.BooleanValue;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class GenericWorkItemPropertyChangeListenerTest extends WorkItemAbstractTest {
    @Test
    public void testPropertyChangeEvent() {
        final PropertyChangeTestWorkItem workItem = new PropertyChangeTestWorkItem();

        getRootWorkItem().addChildWorkItem(workItem);

        final BooleanValue isRaised = new BooleanValue();

        workItem.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                isRaised.value = true;

                assertThat(evt.getSource()).isEqualTo(workItem);
                assertThat(evt.getPropertyName()).isEqualTo("fakeProperty");
                assertThat(evt.getOldValue()).isEqualTo("fakeOldValue");
                assertThat(evt.getNewValue()).isEqualTo("fakeNewValue");
            }
        });

        workItem.initialize();
        workItem.fireEvent();

        assertThat(isRaised.value).isTrue();
    }

    @Test
    public void testRemoveListener() {
        final PropertyChangeTestWorkItem workItem = new PropertyChangeTestWorkItem();

        getRootWorkItem().addChildWorkItem(workItem);

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                fail();
            }
        };

        workItem.addPropertyChangeListener(listener);
        workItem.removePropertyChangeListener(listener);

        workItem.initialize();
        workItem.fireEvent();
    }

    @Test
    public void testRemoveAllListeners() {
        final PropertyChangeTestWorkItem workItem = new PropertyChangeTestWorkItem();

        getRootWorkItem().addChildWorkItem(workItem);

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                fail();
            }
        };

        workItem.addPropertyChangeListener(listener);
        workItem.removeAllPropertyChangeListeners();

        workItem.initialize();
        workItem.fireEvent();
    }
}

class PropertyChangeTestWorkItem extends SimpleWorkItem {
    public PropertyChangeTestWorkItem() {
        super("TEST_WORK_ITEM");
    }

    public void fireEvent() {
        firePropertyChangeEvent("fakeProperty", "fakeOldValue", "fakeNewValue");
    }
}