package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;
import com.apachetune.core.utils.*;
import static org.testng.Assert.*;
import static org.testng.FileAssert.fail;
import org.testng.annotations.*;

import java.beans.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class ActionImplTest {
    @Test
    public void testGetActionSiteClass() throws Exception {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        assertEquals(actionImpl.getActionSiteClass(), FakeActionSite.class);
    }

    @Test
    public void testFailOnSetActionSiteClassWithoutHandler() {
        try {
            new ActionImpl("fakeAction", FakeActionSiteWithoutHandler.class);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testFailOnSetActionSiteClassWithoutPermitMethod() {
        try {
            new ActionImpl("fakeAction", FakeActionSiteWithoutPermitMethod.class);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testFailOnSetActionSiteClassWithDuplicatedHandler() {
        try {
            new ActionImpl("fakeAction", FakeActionSiteWithDuplicatedHandler.class);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testFailOnSetActionSiteClassWithDuplicatedPermitMethod() {
        try {
            new ActionImpl("fakeAction", FakeActionSiteWithDuplicatedPermitMethod.class);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void testGetActionSite() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        ActionSite expActionSite = new FakeActionSiteObject();

        actionImpl.setActionSite(expActionSite);

        assertEquals(actionImpl.getActionSite(), expActionSite);
    }

    @Test
    public void testInitialActionStateIsDisabled() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        assertFalse(actionImpl.isEnabled());
    }

    @Test
    public void testActionEnablingOnSetActiveHandler() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        assertTrue(actionImpl.isEnabled());
    }

    @Test
    public void testActionDisablingOnSetNonActiveHandler() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        assertFalse(actionImpl.isEnabled());
    }

    @Test
    public void testActionDisablingOnSetNullActionSiteObject() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(null);

        assertFalse(actionImpl.isEnabled());
    }

    @Test
    public void testDisableActionStateWhenSetAnotherActionSiteObjectWithDisabledAction() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        assertFalse(actionImpl.isEnabled());
    }

    @Test
    public void testEnableActionStateWhenSetAnotherActionSiteObjectWithEnabledAction() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        actionImpl.setActionSite(new FakeActionSiteObject(false));

        actionImpl.setActionSite(new FakeActionSiteObject(true));

        assertTrue(actionImpl.isEnabled());
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

    @Test
    public void failOnSetActionSiteObjectWithDuplicateHandlers() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
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

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void failOnSetActionSiteObjectWithDuplicatePermitMethods() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
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

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void failOnSetActionSiteObjectWithoutAnnotatedHandlerImplementation() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
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

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void failOnSetActionSiteObjectWithoutAnnotatedPermissionMethodImplementation() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
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

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void failOnAddingActionSiteObjectWithoutRequiredActionSiteInterface() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
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
            
            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void failOnActionPerformWhenDisabled() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        try {
            actionImpl.actionPerformed(null);            

            fail();
        } catch (Exception e) {
            // No-op.
        }
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

        assertTrue(isPerformed.value);
    }

    @Test
    public void testActionSitePropertyEvent() {
        ActionImpl actionImpl = new ActionImpl("fakeAction", FakeActionSite.class);

        final BooleanValue isPerformed = new BooleanValue();

        final FakeActionSiteObject fakeActionSiteObject = new FakeActionSiteObject();

        actionImpl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                assertEquals(evt.getPropertyName(), "actionSite");
                assertNull(evt.getOldValue());
                assertEquals(evt.getNewValue(), fakeActionSiteObject);

                isPerformed.value = true;
            }
        });

        actionImpl.setActionSite(fakeActionSiteObject);

        assertTrue(isPerformed.value);
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
