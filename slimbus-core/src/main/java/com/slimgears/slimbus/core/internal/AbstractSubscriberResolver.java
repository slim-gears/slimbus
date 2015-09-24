package com.slimgears.slimbus.core.internal;

import com.slimgears.slimbus.core.interfaces.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class AbstractSubscriberResolver implements SubscriberResolver {
    public interface SubscriberFactory<S> {
        EventBus.Subscriber create(HandlerInvokerRegistrar registrar, EventBus.Provider provider);
    }

    private final Map<Class, SubscriberFactory> factoryMap = new HashMap<>();

    protected <S> void registerFactory(Class<? extends S> subscriberClass, SubscriberFactory<S> factory) {
        factoryMap.put(subscriberClass, factory);
    }

    @Override
    public <S> EventBus.Subscriber resovle(HandlerInvokerRegistrar registrar, Class<? extends S> subscriberClass, EventBus.Provider<S> provider) {
        if (!factoryMap.containsKey(subscriberClass)) return null;
        return factoryMap.get(subscriberClass).create(registrar, provider);
    }
}
