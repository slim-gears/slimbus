package com.slimgears.slimbus.core.internal;

import com.slimgears.slimbus.core.interfaces.EventBus;

/**
 * Created by Denis on 24/09/2015.
 */
public interface SubscriberResolver {
    <S> EventBus.Subscriber resovle(Class<? extends S> subscriberClass, EventBus.Provider<S> provider);
}
