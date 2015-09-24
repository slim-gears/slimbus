package com.slimgears.slimbus.core;

import com.slimgears.slimbus.core.interfaces.EventBus;
import com.slimgears.slimbus.core.internal.SlimEventBus;
import com.slimgears.slimbus.core.prototype.GeneratedSubscriberResolver;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Denis on 24/09/2015.
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 16, manifest = Config.NONE)
public class SlimEventBusTest {
    private EventBus bus;

    @Before
    public void setUp() {
        bus = new SlimEventBus(GeneratedSubscriberResolver.INSTANCE);
    }

    @Test
    public void testSubscribePublish() {
        DummyHandler handler = new DummyHandler();
        bus.subscribe(handler);
        bus.publish(new DummyEvent(30));
        Assert.assertEquals(1, handler.getReceivedEvents().size());
        Assert.assertEquals(30, handler.getReceivedEvents().get(0).data);
    }
}
