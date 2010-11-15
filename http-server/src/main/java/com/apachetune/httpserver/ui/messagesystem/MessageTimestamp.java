package com.apachetune.httpserver.ui.messagesystem;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * FIXDOC
 */
public class MessageTimestamp {
    private static final MessageTimestamp empty = new EmptyMessageTimestamp();

    private final long value;

    public static MessageTimestamp create(long value) {
        isTrue(value > 0);

        return new MessageTimestamp(value);
    }

    public static MessageTimestamp createEmpty() {
        return empty;
    }

    private MessageTimestamp(long value) {
        this.value = value;
    }

    public final long getValue() {
        isTrue(!isEmpty());

        return value;
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MessageTimestamp that = (MessageTimestamp) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    private static class EmptyMessageTimestamp extends MessageTimestamp {

        private EmptyMessageTimestamp() {
            super(0);
        }

        @Override
        public final boolean isEmpty() {
            return true;
        }
    }
}
