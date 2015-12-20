package com.slimgears.slimbus;

import com.slimgears.slimbus.internal.ClassSubscriber;
import com.slimgears.slimbus.internal.HandlerInvoker;
import com.slimgears.slimbus.internal.HandlerInvokerRegistrar;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedDummyHandlerSubscriber implements ClassSubscriber<DummyHandler> {
    public static final Class<DummyHandler> SUBSCRIBER_CLASS = DummyHandler.class;

    @Override
    public EventBus.Subscription[] subscribe(HandlerInvokerRegistrar registrar, final EventBus.Provider<DummyHandler> provider) {
        return new EventBus.Subscription[]{
                registrar.addInvoker(DummyEvent.class, new HandlerInvoker<DummyEvent>() {
                    @Override
                    public void invoke(DummyEvent event) {
                        provider.provide().onDummyEvent(event);
                    }
                })
        };
    }
}
