package com.slimgears.slimbus;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public interface EventBus {
    interface Provider<S> {
        S provide();
    }

    interface DeliveryCallback<E> {
        void onDelivered(E event);
    }

    interface ErrorHandler<E> {
        void onError(E event, Throwable error);
    }

    interface Unsubscriber {
        void unsubscribe();
    }

    interface Subscriber {
        Unsubscriber subscribe();
    }

    interface Publisher {
        void publish();
    }

    interface PublishBuilderBase<E, B extends PublishBuilderBase<E, B>> extends Publisher {
        B sticky();
        B stickyClearPrevious();
    }

    interface PublishBuilder<E> extends Publisher, PublishBuilderBase<E, PublishBuilder<E>> {
        AsyncPublishBuilder<E> async();
        AsyncPublishBuilder<E> delayed(long millis);
    }

    interface AsyncPublishBuilder<E> extends Publisher, PublishBuilderBase<E, AsyncPublishBuilder<E>> {
        AsyncPublishBuilder<E> onDelivered(DeliveryCallback<E> callback);
        AsyncPublishBuilder<E> onError(ErrorHandler<E> callback);
    }

    <E> void publish(E event);
    <E> PublishBuilder<E> publishBuilder(E event);

    <S> Unsubscriber subscribe(S subscriber);
    <S> Unsubscriber subscribeProvider(Class<S> subscriberClass, Provider<S> provider);
}
