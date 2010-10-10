package com.apachetune.core;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
class FakeEventWorkItem extends SimpleWorkItem {
    private final EventHistory eventHistory;

    /**
     * FIXDOC
     *
     * @param id FIXDOC
     * @param eventHistory FIXDOC
     */
    public FakeEventWorkItem(String id, EventHistory eventHistory) {
        super(id);

        assert eventHistory != null;

        this.eventHistory = eventHistory;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscriber(eventId = "TEST_EVENT")
    private void onTestEvent(Object data) {
        String msg = (String) data;

        eventHistory.addEvent(getId(), msg, "1");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscriber(eventId = "TEST_EVENT")
    private void onTestEventSecondHandler(Object data) {
        String msg = (String) data;

        eventHistory.addEvent(getId(), msg, "2");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Subscriber(eventId = "NON_HANDLING_TEST_EVENT")
    private void onNonHandlingTestEvent(Object data) throws Exception {
        throw new Exception("A non-handling event handler was called.");
    }
}
