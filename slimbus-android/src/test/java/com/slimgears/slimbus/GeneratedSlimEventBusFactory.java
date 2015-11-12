package com.slimgears.slimbus;

import com.slimgears.slimbus.internal.AbstractSubscriberResolver;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedSlimEventBusFactory extends AbstractSubscriberResolver implements SlimEventBusFactory {
    public static final SlimEventBusFactory INSTANCE = new GeneratedSlimEventBusFactory();

    private GeneratedSlimEventBusFactory() {
        addSubscriber(GeneratedDummyHandlerSubscriber.SUBSCRIBER_CLASS, new GeneratedDummyHandlerSubscriber());
    }

    @Override
    public EventBus createEventBus() {
        return new SlimEventBus(this);
    }
}
