package com.slimgears.slimbus.subscribers;

import com.slimgears.slimbus.GeneratedDummyHandlerSubscriber;
import com.slimgears.slimbus.internal.AbstractSubscriberResolver;
import com.slimgears.slimbus.internal.SubscriberResolver;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class GeneratedSubscriberResolver extends AbstractSubscriberResolver {
    public static final SubscriberResolver INSTANCE = new GeneratedSubscriberResolver();

    private GeneratedSubscriberResolver() {
        addSubscriber(GeneratedDummyHandlerSubscriber.SUBSCRIBER_CLASS, new GeneratedDummyHandlerSubscriber());
    }
}
