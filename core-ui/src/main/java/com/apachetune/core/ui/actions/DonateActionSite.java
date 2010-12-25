package com.apachetune.core.ui.actions;

import static com.apachetune.core.ui.Constants.HELP_DONATE_ACTION;

/**
 * FIXDOC
 */
public interface DonateActionSite extends ActionSite {
    @ActionHandler(HELP_DONATE_ACTION)
    void onOpenWebPortalDonatePage();

    @ActionPermission(HELP_DONATE_ACTION)
    boolean isOpenWebPortalDonatePageEnabled();

}
