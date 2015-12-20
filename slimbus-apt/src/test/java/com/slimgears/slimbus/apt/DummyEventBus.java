// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimbus.apt;

import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.internal.SubscriberResolver;

/**
 * Created by ditskovi on 11/11/2015.
 *
 */
public class DummyEventBus implements EventBus {
    public DummyEventBus(SubscriberResolver... resolvers) {

    }

    @Override
    public <E> void publish(E event) {

    }

    @Override
    public <E> PublishBuilder<E> publishBuilder(E event) {
        return null;
    }

    @Override
    public void clearSticky(Class eventClass) {

    }

    @Override
    public <S> Subscription subscribe(S subscriber) {
        return null;
    }

    @Override
    public <S> Subscription subscribeProvider(Class<S> subscriberClass, Provider<S> provider) {
        return null;
    }
}
