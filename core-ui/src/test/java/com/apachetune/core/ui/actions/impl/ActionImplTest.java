package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.ActionHandler;
import com.apachetune.core.ui.actions.ActionPermission;
import com.apachetune.core.ui.actions.ActionSite;
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
public class ActionImplTest {
    @Test
    public void testGetActionSiteClass() throws Exception {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        assertThat(actionImpl.getActionSiteClass()).isEqualTo(FakeActionSite.class);
    }

    @Test(expected = Exception.class)
    public void testFailOnSetActionSiteClassWithoutHandler() {
        new ActionImpl("fakeAction", FakeActionSiteWithoutHandler.class);
    }

    @Test(expected = Exception.class)
    public void testFailOnSetActionSiteClassWithoutPermitMethod() {
        new ActionImpl("fakeAction", FakeActionSiteWithoutPermitMethod.class);
    }

    @Test(expected = Exception.class)
    public void testFailOnSetActionSiteClassWithDuplicatedHandler() {
        new ActionImpl("fakeAction", FakeActionSiteWithDuplicatedHandler.class);
    }

    @Test(expected = Exception.class)
    public void testFailOnSetActionSiteClassWithDuplicatedPermitMethod() {
        new ActionImpl("fakeAction", FakeActionSiteWithDuplicatedPermitMethod.class);
    }

    @Test
    public void testGetActionSite() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        ActionSite expActionSite = new FakeActionSiteObject();

        actionImpl.setActionSite(expActionSite);

        assertThat(actionImpl.getActionSite()).isEqualTo(expActionSite);
    }

    @Test
    public void testInitialActionStateIsDisabled() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        assertThat(actionImpl.isEnabled()).isFalse();
    }

    @Test
    public void testActionEnablingOnSetActiveHandler() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        assertThat(actionImpl.isEnabled()).isTrue();
    }

    @Test
    public void testActionDisablingOnSetNonActiveHandler() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        assertThat(actionImpl.isEnabled()).isFalse();
    }

    @Test
    public void testActionDisablingOnSetNullActionSiteObject() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(null);

        assertThat(actionImpl.isEnabled()).isFalse();
    }

    @Test
    public void testDisableActionStateWhenSetAnotherActionSiteObjectWithDisabledAction() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        assertThat(actionImpl.isEnabled()).isFalse();
    }

    @Test
    public void testEnableActionStateWhenSetAnotherActionSiteObjectWithEnabledAction() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        assertThat(actionImpl.isEnabled()).isTrue();
    }

    @Test
    public void testFailOnEnableOperation() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
            actionImpl.setEnabled(true);

            fail();
        } catch (Exception e) {
            // No-op.
        }

        try {
            actionImpl.setEnabled(false);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test(expected = Exception.class)
    public void failOnSetActionSiteObjectWithDuplicateHandlers() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSite() {
            public void onTestAction() {
                // No-op.
            }

            public boolean isTestActionEnabled() {
                return false;
            }

            @ActionHandler("fakeAction")
            public void onTestAction2() {
                // No-op.
            }
        });
    }

    @Test(expected = Exception.class)
    public void failOnSetActionSiteObjectWithDuplicatePermitMethods() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSite() {
            public void onTestAction() {
                // No-op.
            }

            public boolean isTestActionEnabled() {
                return false;
            }

            @ActionPermission("fakeAction")
            public boolean isTestActionEnabled2() {
                return false;
            }
        });
    }

    @Test(expected = Exception.class)
    public void failOnSetActionSiteObjectWithoutAnnotatedHandlerImplementation() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSite() {
            public void onTestAction() {
                // No-op.
            }

            @ActionPermission("fakeAction")
            public boolean isTestActionEnabled() {
                return false;
            }

            @ActionHandler("fakeAction")
            public void onTestAction2() {
                // No-op.
            }
        });
    }

    @Test(expected = Exception.class)
    public void failOnSetActionSiteObjectWithoutAnnotatedPermissionMethodImplementation() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSite() {
            @ActionHandler("fakeAction")
            public void onTestAction() {
                // No-op.
            }

            public boolean isTestActionEnabled() {
                return false;
            }

            @ActionPermission("fakeAction")
            public boolean isTestActionEnabled2() {
                return false;
            }
        });
    }

    @Test(expected = Exception.class)
    public void failOnAddingActionSiteObjectWithoutRequiredActionSiteInterface() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new ActionSite() {
            @ActionHandler("fakeAction")
            public void onTestAction() {
                // No-op.
            }

            @ActionPermission("fakeAction")
            public boolean isTestActionEnabled() {
                return false;
            }
        });
    }

    @Test(expected = Exception.class)
    public void failOnActionPerformWhenDisabled() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.actionPerformed(null);
    }

    @Test
    public void testPerformAction() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        final BooleanValue isPerformed = new BooleanValue();

        actionImpl.setActionSite(new FakeActionSite() {
            @ActionHandler("fakeAction")
            public void onTestAction() {
                isPerformed.value = true;    
            }

            @ActionPermission("fakeAction")
            public boolean isTestActionEnabled() {
                return true;
            }
        });

        actionImpl.actionPerformed(null);

        assertThat(isPerformed.value).isTrue();
    }

    @Test
    public void testActionSitePropertyEvent() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        final BooleanValue isPerformed = new BooleanValue();

        final FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject();

        actionImpl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                assertThat(evt.getPropertyName()).isEqualTo("actionSite");
                assertThat(evt.getOldValue()).isNull();
                assertThat(evt.getNewValue()).isEqualTo(fakeActionSiteObject);

                isPerformed.value = true;
            }
        });

        actionImpl.setActionSite(fakeActionSiteObject);

        assertThat(isPerformed.value).isTrue();
    }
}

interface FakeActionSiteWithoutHandler extends ActionSite {
    @ActionPermission("fakeAction")
    boolean isTestActionEnabled();
}

interface FakeActionSiteWithoutPermitMethod extends ActionSite {
    @ActionHandler("fakeAction")
    void onTestAction();
}

interface FakeActionSiteWithDuplicatedHandler extends ActionSite {
    @ActionHandler("fakeAction")
    void onTestAction();

    @ActionHandler("fakeAction")
    void onTestAction2();

    @ActionPermission("fakeAction")
    boolean isTestActionEnabled();
}

interface FakeActionSiteWithDuplicatedPermitMethod extends ActionSite {
    @ActionHandler("fakeAction")
    void onTestAction();

    @ActionPermission("fakeAction")
    boolean isTestActionEnabled();

    @ActionPermission("fakeAction")
    boolean isTestActionEnabled2();
}

class FakeActionSiteObject implements FakeActionSite {
    private boolean isTestActionEnabled;


    public FakeActionSiteObject() {
        // No-op.
    }

    public FakeActionSiteObject(boolean testActionEnabled) {
        isTestActionEnabled = testActionEnabled;
    }

    @ActionHandler("fakeAction")
    public void onTestAction() {
        // No-op.
    }

    @ActionPermission("fakeAction")
    public boolean isTestActionEnabled() {
        return isTestActionEnabled;
    }

    public void setTestActionEnabled(boolean testActionEnabled) {
        isTestActionEnabled = testActionEnabled;
    }
}
