package com.slimgears.slimbus;

import com.slimgears.slimbus.internal.AbstractSubscriberResolver;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedBusFactory extends AbstractSubscriberResolver implements BusFactory {
    public static final BusFactory INSTANCE = new GeneratedBusFactory();

    private GeneratedBusFactory() {
        addSubscriber(GeneratedDummyHandlerSubscriber.SUBSCRIBER_CLASS, new GeneratedDummyHandlerSubscriber());
    }

    @Override
    public EventBus createEventBus() {
        return new SlimEventBus(this);
    }
}
