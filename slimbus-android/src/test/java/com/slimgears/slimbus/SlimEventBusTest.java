package com.slimgears.slimbus;

import com.slimgears.slimbus.subscribers.GeneratedSubscriberResolver;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 16, manifest = Config.NONE)
public class SlimEventBusTest {
    interface Assertion<T> {
        void validate(T arg);
    }

    private EventBus bus;
    private DummyEvent lastEvent;
    private List<DummyEvent> asyncDeliveredEvents = new ArrayList<>();
    private List<DummyHandler> handlers = new ArrayList<>();

    @Before
    public void setUp() {
        bus = new SlimEventBus(GeneratedSubscriberResolver.INSTANCE);
    }

    @Test
    public void testSubscribeThenPublishShouldReceive() {
        DummyHandler handler = newHandler();
        bus.subscribe(handler);
        bus.publish(newEvent(30));
        Assert.assertEquals(1, handler.receivedEvents.size());
        Assert.assertEquals(30, handler.receivedEvents.get(0).data);

        bus.publish(newEvent(40));
        Assert.assertEquals(2, handler.receivedEvents.size());
        Assert.assertEquals(40, handler.receivedEvents.get(1).data);
    }

    @Test
    public void testSubscribeProviderThenPublishShouldReceive() {
        bus.subscribeProvider(DummyHandler.class, this::newHandler);

        bus.publish(newEvent(30));
        bus.publish(newEvent(40));

        Assert.assertEquals(2, handlers.size());
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, lastHandler().receivedEvents.size());
        Assert.assertEquals(30, firstHandler().receivedEvents.get(0).data);
        Assert.assertEquals(40, lastHandler().receivedEvents.get(0).data);
    }

    @Test
    public void testPublishThenSubscribeShouldNotReceive() {
        DummyHandler handler = new DummyHandler();
        bus.publish(new DummyEvent(20));
        bus.subscribe(handler);
        Assert.assertEquals(0, handler.receivedEvents.size());
    }

    @Test
    public void testPublishStickyMultipleTimesThenSubscribeShouldReceiveMultipleTimes() {
        bus.publishSticky(new DummyEvent(10));
        bus.subscribe(newHandler());

        Assert.assertEquals(1, handlers.get(0).receivedEvents.size());
        bus.subscribe(newHandler());

        forAllHandlers(h -> Assert.assertEquals(1, h.receivedEvents.size()));

        bus.publishSticky(new DummyEvent(20));
        bus.subscribe(newHandler());

        forAllHandlers(h -> Assert.assertEquals(2, h.receivedEvents.size()));
    }

    @Test
    public void testPublishStickyThenPublishSingleStickyThenSubscribeShouldReceiveOnce() {
        bus.publishSticky(newEvent(10));
        bus.publishSingleSticky(newEvent(20));

        bus.subscribe(newHandler());
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(20, firstHandler().receivedEvents.get(0).data);
    }

    @Test
    public void testSubscribeThenPublishAsyncSubscriberShouldReceiveAsynchronously() {
        bus.subscribe(newHandler());
        Robolectric.getForegroundThreadScheduler().pause();
        bus.publishAsync(newEvent(20), this::onEventDelivered);
        Assert.assertEquals(0, firstHandler().receivedEvents.size());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, asyncDeliveredEvents.size());
    }

    @Test
    public void testSubscribePublishDelayedShouldReceiveAsynchronously() {
        bus.subscribe(newHandler());
        bus.publishDelayed(newEvent(10), 9999);
        bus.publishDelayed(newEvent(20), 10000, this::onEventDelivered);
        Assert.assertEquals(0, firstHandler().receivedEvents.size());
        Robolectric.getForegroundThreadScheduler().advanceBy(10001);
        Assert.assertEquals(2, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, asyncDeliveredEvents.size());
    }

    private DummyEvent newEvent(int data) {
        return lastEvent = new DummyEvent(data);
    }

    private DummyEvent newEvent() {
        return newEvent(handlers.size() * 10);
    }

    private void onEventDelivered(DummyEvent event) {
        asyncDeliveredEvents.add(event);
    }

    private DummyHandler newHandler() {
        DummyHandler handler = new DummyHandler();
        handlers.add(handler);
        return handler;
    }

    private DummyHandler firstHandler() {
        return handlers.get(0);
    }

    private DummyHandler lastHandler() {
        return handlers.get(handlers.size() - 1);
    }

    private void forAllHandlers(Assertion<DummyHandler> handlerAssertion) {
        for (DummyHandler handler : handlers) {
            handlerAssertion.validate(handler);
        }
    }
}
