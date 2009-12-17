package com.apachetune.core.ui.actions.impl;

import com.apachetune.core.ui.actions.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
interface FakeActionSite extends ActionSite {
    @ActionHandler("fakeAction")
    void onTestAction();

    @ActionPermission("fakeAction")
    boolean isTestActionEnabled();
}

interface FakeActionSite1 extends ActionSite {
    @ActionHandler("fakeAction1")
    void onTestAction();

    @ActionPermission("fakeAction1")
    boolean isTestActionEnabled();
}

interface FakeActionSite2 extends ActionSite {
    @ActionHandler("fakeAction2")
    void onTestAction();

    @ActionPermission("fakeAction2")
    boolean isTestActionEnabled();
}

interface FakeActionSite3 extends ActionSite {
    @ActionHandler("fakeAction3")
    void onTestAction();

    @ActionPermission("fakeAction3")
    boolean isTestActionEnabled();
}
