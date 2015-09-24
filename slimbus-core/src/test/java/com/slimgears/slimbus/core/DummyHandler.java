package com.slimgears.slimbus.core;

import com.slimgears.slimbus.core.annotations.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class DummyHandler {
    private final List<DummyEvent> receivedEvents = new ArrayList<>();

    @Subscribe
    public void onDummyEvent(DummyEvent event) {
        receivedEvents.add(event);
    }

    public List<DummyEvent> getReceivedEvents() {
        return receivedEvents;
    }
}
