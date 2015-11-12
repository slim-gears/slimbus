package com.slimgears.slimbus;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    private EventBus.Provider<DummyHandler> newHandlerProvider = new EventBus.Provider<DummyHandler>() {
        @Override
        public DummyHandler provide() {
            return newHandler();
        }
    };

    private EventBus.DeliveryCallback<DummyEvent> onDeliveredCallback = new EventBus.DeliveryCallback<DummyEvent>() {
        @Override
        public void onDelivered(DummyEvent event) {
            onEventDelivered(event);
        }
    };

    @Before
    public void setUp() {
        bus = GeneratedBusFactory.INSTANCE.createEventBus();
    }

    @Test
    public void testSubscribeThenPublishShouldReceive() {
        DummyHandler handler = newHandler();
        bus.subscribe(handler);
        bus.publish(newEvent(30));
        Assert.assertEquals(1, handler.receivedEvents.size());
        Assert.assertEquals(lastEvent.data, handler.receivedEvents.get(0).data);

        bus.publish(newEvent(40));
        Assert.assertEquals(2, handler.receivedEvents.size());
        Assert.assertEquals(lastEvent.data, handler.receivedEvents.get(1).data);
    }

    @Test
    public void testSubscribeProviderThenPublishShouldReceive() {
        bus.subscribeProvider(DummyHandler.class, newHandlerProvider);

        bus.publish(newEvent(30));
        bus.publish(newEvent(40));

        Assert.assertEquals(2, handlers.size());
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, lastHandler().receivedEvents.size());
        Assert.assertEquals(30, firstHandler().receivedEvents.get(0).data);
        Assert.assertEquals(lastEvent.data, lastHandler().receivedEvents.get(0).data);
    }

    @Test
    public void testPublishThenSubscribeShouldNotReceive() {
        DummyHandler handler = new DummyHandler();
        bus.publish(newEvent());
        bus.subscribe(handler);
        Assert.assertEquals(0, handler.receivedEvents.size());
    }

    @Test
    public void testPublishStickyMultipleTimesThenSubscribeShouldReceiveMultipleTimes() {
        bus.publishBuilder(newEvent(10)).sticky().publish();
        bus.subscribe(newHandler());

        Assert.assertEquals(1, handlers.get(0).receivedEvents.size());
        bus.subscribe(newHandler());

        Assert.assertEquals(1, handlers.get(0).receivedEvents.size());
        Assert.assertEquals(1, handlers.get(1).receivedEvents.size());

        bus.publishBuilder(newEvent(20)).sticky().publish();
        bus.subscribe(newHandler());

        Assert.assertEquals(2, handlers.get(0).receivedEvents.size());
        Assert.assertEquals(2, handlers.get(1).receivedEvents.size());
        Assert.assertEquals(2, handlers.get(2).receivedEvents.size());
    }

    @Test
    public void testPublishStickyThenPublishSingleStickyThenSubscribeShouldReceiveOnce() {
        bus.publishBuilder(newEvent(10)).sticky().publish();
        bus.publishBuilder(newEvent(20)).stickyClearPrevious().publish();

        bus.subscribe(newHandler());
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(lastEvent.data, firstHandler().receivedEvents.get(0).data);
    }

    @Test
    public void testSubscribeThenPublishAsyncSubscriberShouldReceiveAsynchronously() {
        bus.subscribe(newHandler());
        Robolectric.getForegroundThreadScheduler().pause();
        bus.publishBuilder(newEvent()).async().onDelivered(onDeliveredCallback).publish();
        Assert.assertEquals(0, firstHandler().receivedEvents.size());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertEquals(1, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, asyncDeliveredEvents.size());
    }

    @Test
    public void testSubscribePublishDelayedShouldReceiveAsynchronously() {
        bus.subscribe(newHandler());
        bus.publishBuilder(newEvent()).delayed(9999).publish();
        bus.publishBuilder(newEvent()).delayed(10000).onDelivered(onDeliveredCallback).publish();
        Assert.assertEquals(0, firstHandler().receivedEvents.size());
        Robolectric.getForegroundThreadScheduler().advanceBy(10001);
        Assert.assertEquals(2, firstHandler().receivedEvents.size());
        Assert.assertEquals(1, asyncDeliveredEvents.size());
    }

    @Test
    public void testErrorDuringAsyncPublishHandlerShouldBeCalled() {
        bus.subscribe(newHandler(new DummyHandler.Callback() {
            @Override
            public void onEvent(DummyEvent event) {
                throw new RuntimeException();
            }
        }));
        //noinspection unchecked
        EventBus.ErrorHandler<DummyEvent> errorHandler = mock(EventBus.ErrorHandler.class);
        Robolectric.getForegroundThreadScheduler().pause();
        bus.publishBuilder(newEvent()).async().onError(errorHandler).publish();
        Robolectric.flushForegroundThreadScheduler();
        verify(errorHandler).onError(any(DummyEvent.class), any(Throwable.class));
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
        return addHandler(new DummyHandler());
    }

    private DummyHandler newHandler(DummyHandler.Callback callback) {
        return addHandler(new DummyHandler(callback));
    }

    private DummyHandler addHandler(DummyHandler handler) {
        handlers.add(handler);
        return handler;
    }

    private DummyHandler firstHandler() {
        return handlers.get(0);
    }

    private DummyHandler lastHandler() {
        return handlers.get(handlers.size() - 1);
    }
}
