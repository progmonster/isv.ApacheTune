package com.apachetune.core;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
class TestEventWorkItem extends SimpleWorkItem {
    private final EventHistory eventHistory;

    /**
     * FIXDOC
     *
     * @param id FIXDOC
     * @param eventHistory FIXDOC
     */
    public TestEventWorkItem(String id, EventHistory eventHistory) {
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
    private void onNonHandlingTestEvent(Object data) {
        org.testng.Assert.fail("A non-handling event handler was called.");
    }
}
