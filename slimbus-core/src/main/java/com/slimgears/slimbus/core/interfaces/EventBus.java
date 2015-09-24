package com.slimgears.slimbus.core.interfaces;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public interface EventBus {
    interface Provider<S> {
        S provide();
    }

    interface PublishCallback<E> {
        void onDelivered(E event);
    }

    interface Unsubscriber {
        void unsubscribe();
    }

    interface Subscriber {
        Unsubscriber subscribe();
    }

    <E> void publish(E event);
    <E> void publishAsync(E event, PublishCallback<E> callback);
    <E> void publishSticky(E event);
    <E> void publishDelayed(E event, long delayMilliseconds);
    <E> void publishDelayed(E event, long delayMilliseconds, PublishCallback<E> callback);

    <S> Unsubscriber subscribe(S subscriber);
    <S> Unsubscriber subscribeProvider(Class<S> subscriberClass, Provider<S> provider);
}
