package com.apachetune.httpserver.ui.updating;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.net.URL;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * FIXDOC
 */
public class UpdateInfo {
    private final boolean hasUpdate;

    private final String userFriendlyFullAppName;

    private final URL userFriendlyUpdatePageUrl;

    public static UpdateInfo createNoUpdateInfo() {
        return new UpdateInfo(false, null, null);
    }

    public static UpdateInfo create(String userFriendlyFullAppName, URL userFriendlyUpdatePageUrl) {
        return new UpdateInfo(true, userFriendlyFullAppName, userFriendlyUpdatePageUrl);
    }

    private UpdateInfo(boolean hasUpdate, String userFriendlyFullAppName, URL userFriendlyUpdatePageUrl) {
        this.hasUpdate = hasUpdate;
        this.userFriendlyFullAppName = userFriendlyFullAppName;
        this.userFriendlyUpdatePageUrl = userFriendlyUpdatePageUrl;
    }

    public final boolean hasUpdate() {
        return hasUpdate;
    }

    public final String getUserFriendlyFullAppName() {
        isTrue(hasUpdate);
        
        return userFriendlyFullAppName;
    }

    public final URL getUserFriendlyUpdatePageUrl() {
        isTrue(hasUpdate);
        
        return userFriendlyUpdatePageUrl;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateInfo that = (UpdateInfo) o;

        return hasUpdate == that.hasUpdate &&
                !(userFriendlyFullAppName != null ? !userFriendlyFullAppName.equals(that.userFriendlyFullAppName) :
                        that.userFriendlyFullAppName != null) && !(userFriendlyUpdatePageUrl != null ?
                !userFriendlyUpdatePageUrl.equals(that.userFriendlyUpdatePageUrl) :
                that.userFriendlyUpdatePageUrl != null);

    }

    @Override
    public final int hashCode() {
        int result = (hasUpdate ? 1 : 0);
        result = 31 * result + (userFriendlyFullAppName != null ? userFriendlyFullAppName.hashCode() : 0);
        result = 31 * result + (userFriendlyUpdatePageUrl != null ? userFriendlyUpdatePageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this).
                append("hasUpdate", hasUpdate).
                append("userFriendlyFullAppName", userFriendlyFullAppName).
                append("userFriendlyUpdatePageUrl", userFriendlyUpdatePageUrl).
                toString();
    }
}
