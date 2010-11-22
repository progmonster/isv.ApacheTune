package com.apachetune.httpserver.ui.updating;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.net.URL;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * FIXDOC
 */
public class UpdateInfo {
    private final boolean hasUpdate;

    private final URL userFriendlyUpdatePageUrl;

    public static UpdateInfo createNoUpdateInfo() {
        return new UpdateInfo(false, null);
    }

    public static UpdateInfo create(URL userFriendlyUpdatePageUrl) {
        return new UpdateInfo(true, userFriendlyUpdatePageUrl);
    }

    private UpdateInfo(boolean hasUpdate, URL userFriendlyUpdatePageUrl) {
        this.hasUpdate = hasUpdate;
        this.userFriendlyUpdatePageUrl = userFriendlyUpdatePageUrl;
    }

    public final boolean hasUpdate() {
        return hasUpdate;
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

        return hasUpdate == that.hasUpdate && !(userFriendlyUpdatePageUrl != null ?
                !userFriendlyUpdatePageUrl.equals(that.userFriendlyUpdatePageUrl) :
                that.userFriendlyUpdatePageUrl != null);

    }

    @Override
    public final int hashCode() {
        int result = (hasUpdate ? 1 : 0);
        result = 31 * result + (userFriendlyUpdatePageUrl != null ? userFriendlyUpdatePageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this).
                append("hasUpdate", hasUpdate).
                append("userFriendlyUpdatePageUrl", userFriendlyUpdatePageUrl).
                toString();
    }
}
