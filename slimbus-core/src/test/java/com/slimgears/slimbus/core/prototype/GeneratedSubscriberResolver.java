package com.slimgears.slimbus.core.prototype;

import com.slimgears.slimbus.core.DummyHandler;
import com.slimgears.slimbus.core.internal.AbstractSubscriberResolver;
import com.slimgears.slimbus.core.internal.SubscriberResolver;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedSubscriberResolver extends AbstractSubscriberResolver {
    public static final SubscriberResolver INSTANCE = new GeneratedSubscriberResolver();

    private GeneratedSubscriberResolver() {
        registerFactory(DummyHandler.class, GeneratedDummyHandlerSubscriber::new);
    }
}
