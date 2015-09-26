package com.slimgears.slimbus.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class AbstractSubscriberResolver implements SubscriberResolver {
    private final Map<Class, ClassSubscriber> subscriberMap = new HashMap<>();

    protected <S> void addSubscriber(Class<? extends S> subscriberClass, ClassSubscriber<S> classSubscriber) {
        subscriberMap.put(subscriberClass, classSubscriber);
    }

    @Override
    public <S> ClassSubscriber<S> resovle(Class<? extends S> subscriberClass) {
        if (!subscriberMap.containsKey(subscriberClass)) return null;
        //noinspection unchecked
        return subscriberMap.get(subscriberClass);
    }
}
