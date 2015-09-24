package com.slimgears.slimbus.core.internal;

/**
 * Created by Denis on 24/09/2015.
 */
public interface ListenerInvoker<E> {
    void invoke(E event);
}
