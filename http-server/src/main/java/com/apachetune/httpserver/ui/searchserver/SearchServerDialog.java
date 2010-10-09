package com.apachetune.httpserver.ui.searchserver;

import com.apachetune.core.ui.SmartPart;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public interface SearchServerDialog extends SmartPart {
    void setDrivesAvailableToSearch(Collection<File> drives);

    List<File> getSelectedDrivesToSearch();

    void setSourceTableEnabled(boolean isEnabled);

    void setStartSearchButtonEnabled(boolean isEnabled);

    void setStopSearchButtonEnabled(boolean isEnabled);

    void setSelectServerButtonEnabled(boolean isEnabled);

    void setResultListModel(ResultListModel model);

    void clearSearchResults();

    void setSearchProgressBarRun(boolean isRun);

    void setCurrentSearchLocationText(String location);

    void selectFirstResult();
}
