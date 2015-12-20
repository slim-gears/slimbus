package com.slimgears.slimbus.internal;

import com.slimgears.slimbus.EventBus;

/**
 * Created by Denis on 25/09/2015.
 */
public interface ClassSubscriber<S> {
    EventBus.Subscription[] subscribe(HandlerInvokerRegistrar registrar, EventBus.Provider<S> provider);
}
