package com.apachetune.httpserver.ui.messagesystem;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * FIXDOC
 */
public class NewsMessage {
    private MessageTimestamp timestamp;

    private String subject;

    private String content;

    private boolean isUnread;

    public static Builder createBuilder() {
        return new Builder();
    }

    public NewsMessage(MessageTimestamp timestamp, String subject, String content, boolean unread) {
        this.timestamp = timestamp;
        this.subject = subject;
        this.content = content;
        isUnread = unread;
    }

    private NewsMessage(NewsMessage msg) {
        this(msg.getTimestamp(), msg.getSubject(), msg.getContent(), msg.isUnread());
    }

    public final MessageTimestamp getTimestamp() {
        return timestamp;
    }

    public final String getSubject() {
        return subject;
    }

    public final String getContent() {
        return content;
    }

    public final boolean isUnread() {
        return isUnread;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsMessage that = (NewsMessage) o;

        return timestamp.equals(that.timestamp);
    }

    @Override
    public final int hashCode() {
        return timestamp.hashCode();
    }

    @Override
    public final String toString() {
        //noinspection DuplicateStringLiteralInspection
        return new ToStringBuilder(this).
                append("timestamp", timestamp). //NON-NLS
                append("subject", subject). //NON-NLS
                append("content", content). //NON-NLS
                append("isUnread", isUnread). //NON-NLS
                toString();
    }

    private void setTimestamp(MessageTimestamp timestamp) {
        this.timestamp = timestamp;
    }

    private void setSubject(String subject) {
        this.subject = subject;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setUnread(boolean unread) {
        isUnread = unread;
    }

    public static class Builder {
        private NewsMessage msg = new NewsMessage(MessageTimestamp.createEmpty(), null, null, false);

        private Builder() {
            // No-op.
        }

        public final Builder copyFrom(NewsMessage msg) {
            this.msg = new NewsMessage(msg);

            return this;
        }

        public final Builder setTimestamp(MessageTimestamp timestamp) {
            msg.setTimestamp(timestamp);

            return this;
        }

        public final Builder setSubject(String subject) {
            msg.setSubject(subject);

            return this;
        }

        public final Builder setContent(String content) {
            msg.setContent(content);

            return this;
        }

        public final Builder setUnread(boolean unread) {
            msg.setUnread(unread);

            return this;
        }

        public final NewsMessage build() {
            return new NewsMessage(msg);
        }
    }
}
