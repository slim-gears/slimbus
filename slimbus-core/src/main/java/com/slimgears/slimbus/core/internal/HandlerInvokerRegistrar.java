package com.slimgears.slimbus.core.internal;

import com.slimgears.slimbus.core.interfaces.EventBus;

/**
 * Created by Denis on 24/09/2015.
 */
public interface HandlerInvokerRegistrar {
    <E> EventBus.Unsubscriber addInvoker(Class<E> eventClass, HandlerInvoker<E> handlerInvoker);
}
