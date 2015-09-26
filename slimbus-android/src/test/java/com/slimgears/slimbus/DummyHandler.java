package com.slimgears.slimbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
class DummyHandler {
    interface Callback {
        void onEvent(DummyEvent event);
    }

    public final List<DummyEvent> receivedEvents = new ArrayList<>();
    public final Callback callback;

    public DummyHandler() {
        this(null);
    }

    public DummyHandler(Callback callback) {
        this.callback = callback;
    }

    @Subscribe
    public void onDummyEvent(DummyEvent event) {
        receivedEvents.add(event);
        if (callback != null) {
            callback.onEvent(event);
        }
    }
}
