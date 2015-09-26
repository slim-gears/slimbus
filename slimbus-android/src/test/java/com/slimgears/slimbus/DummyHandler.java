package com.slimgears.slimbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
class DummyHandler {
    public final List<DummyEvent> receivedEvents = new ArrayList<>();

    @Subscribe
    public void onDummyEvent(DummyEvent event) {
        receivedEvents.add(event);
    }
}
