package com.slimgears.slimbus.internal;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public interface HandlerInvoker<E> {
    void invoke(E event);
}
