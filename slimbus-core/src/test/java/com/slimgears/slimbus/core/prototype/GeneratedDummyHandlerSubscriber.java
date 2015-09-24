package com.slimgears.slimbus.core.prototype;

import com.slimgears.slimbus.core.DummyEvent;
import com.slimgears.slimbus.core.DummyHandler;
import com.slimgears.slimbus.core.interfaces.EventBus;
import com.slimgears.slimbus.core.internal.HandlerInvokerRegistrar;
import com.slimgears.slimbus.core.internal.SlimEventBus;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedDummyHandlerSubscriber extends SlimEventBus.AbstractObjectSubscriber {
    private final EventBus.Provider<DummyHandler> provider;

    public GeneratedDummyHandlerSubscriber(HandlerInvokerRegistrar registrar, EventBus.Provider<DummyHandler> provider) {
        super(registrar);
        this.provider = provider;
    }

    @Override
    protected EventBus.Unsubscriber[] addInvokers(HandlerInvokerRegistrar registrar) {
        return new EventBus.Unsubscriber[] {
                registrar.addInvoker(DummyEvent.class, e -> provider.provide().onDummyEvent(e))
        };
    }
}
