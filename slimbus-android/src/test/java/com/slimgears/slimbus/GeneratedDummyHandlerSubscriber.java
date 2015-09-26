package com.slimgears.slimbus;

import com.slimgears.slimbus.internal.HandlerInvokerRegistrar;
import com.slimgears.slimbus.internal.ClassSubscriber;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedDummyHandlerSubscriber implements ClassSubscriber<DummyHandler> {
    public static final Class<DummyHandler> SUBSCRIBER_CLASS = DummyHandler.class;

    @Override
    public EventBus.Unsubscriber[] subscribe(HandlerInvokerRegistrar registrar, EventBus.Provider<DummyHandler> provider) {
        return new EventBus.Unsubscriber[] {
                registrar.addInvoker(DummyEvent.class, e -> provider.provide().onDummyEvent(e))
        };
    }
}
