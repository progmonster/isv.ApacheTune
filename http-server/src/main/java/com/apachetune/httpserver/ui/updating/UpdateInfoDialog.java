package com.apachetune.httpserver.ui.updating;

/**
 * FIXDOC
 */
public interface UpdateInfoDialog {
    UserActionOnUpdate showHasUpdate(UpdateInfo updateInfo);

    UserActionOnNoUpdate showHasNoUpdate();

    UserActionOnUpdateError showUpdateCheckingError(UpdateException e);

    public static class UserActionOnUpdate {
        private final boolean isUserAgreeUpdate;

        private final boolean isUserEnableCheckForUpdateOnStart;

        public UserActionOnUpdate(boolean userAgreeUpdate, boolean userEnableCheckForUpdateOnStart) {
            isUserAgreeUpdate = userAgreeUpdate;
            isUserEnableCheckForUpdateOnStart = userEnableCheckForUpdateOnStart;
        }

        public final boolean isUserAgreeUpdate() {
            return isUserAgreeUpdate;
        }

        public final boolean isUserEnableCheckForUpdateOnStart() {
            return isUserEnableCheckForUpdateOnStart;
        }
    }

    public static class UserActionOnNoUpdate {
        private final boolean isUserEnableCheckForUpdateOnStart;

        public UserActionOnNoUpdate(boolean userEnableCheckForUpdateOnStart) {
            isUserEnableCheckForUpdateOnStart = userEnableCheckForUpdateOnStart;
        }

        public final boolean isUserEnableCheckForUpdateOnStart() {
            return isUserEnableCheckForUpdateOnStart;
        }
    }

    public static class UserActionOnUpdateError {
        private final boolean isUserAgreeSendErrorReport;

        public UserActionOnUpdateError(boolean userAgreeSendErrorReport) {
            isUserAgreeSendErrorReport = userAgreeSendErrorReport;
        }

        public final boolean isUserAgreeSendErrorReport() {
            return isUserAgreeSendErrorReport;
        }
    }
}
