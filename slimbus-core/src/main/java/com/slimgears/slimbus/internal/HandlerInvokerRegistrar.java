package com.slimgears.slimbus.internal;

import com.slimgears.slimbus.EventBus;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public interface HandlerInvokerRegistrar {
    <E> EventBus.Subscription addInvoker(Class<E> eventClass, HandlerInvoker<E> handlerInvoker);
}
