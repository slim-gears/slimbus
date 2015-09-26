package com.slimgears.slimbus.internal;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public interface SubscriberResolver {
    <S> ClassSubscriber<S> resovle(Class<? extends S> subscriberClass);
}
