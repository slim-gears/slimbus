package com.slimgears.slimbus;

import android.os.Handler;

import com.slimgears.slimbus.internal.ClassSubscriber;
import com.slimgears.slimbus.internal.HandlerInvoker;
import com.slimgears.slimbus.internal.HandlerInvokerRegistrar;
import com.slimgears.slimbus.internal.ListMap;
import com.slimgears.slimbus.internal.SubscriberResolver;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class SlimEventBus implements EventBus, HandlerInvokerRegistrar {
    private final static Subscriber EMPTY_SUBSCRIBER = () -> () -> {};
    private final ListMap<Class, HandlerInvoker> invokerMap = new ListMap<>();
    private final ListMap<Class, Object> stuckEventsMap = new ListMap<>();
    private final Handler handler = new Handler();
    private final List<SubscriberResolver> resolvers;

    class PublishSpec<E> {
        boolean sticky = false;
        boolean clearPrevious = false;
        boolean async = true;
        long delay = 0;
        DeliveryCallback<E> deliveryCallback;
        ErrorHandler<E> errorHandler;
        final E event;

        PublishSpec(E event) {
            this.event = event;
        }

        PublishBuilder<E> builder() {
            return new Builder();
        }

        class Builder implements PublishBuilder<E> {
            class Async implements AsyncPublishBuilder<E> {
                @Override
                public AsyncPublishBuilder<E> onDelivered(DeliveryCallback<E> callback) {
                    PublishSpec.this.deliveryCallback = callback;
                    return this;
                }

                @Override
                public AsyncPublishBuilder<E> onError(ErrorHandler<E> callback) {
                    errorHandler = callback;
                    return this;
                }

                @Override
                public AsyncPublishBuilder<E> sticky() {
                    Builder.this.sticky();
                    return this;
                }

                @Override
                public AsyncPublishBuilder<E> stickyClearPrevious() {
                    Builder.this.stickyClearPrevious();
                    return this;
                }

                @Override
                public void publish() {
                    Builder.this.publish();
                }
            }

            @Override
            public PublishBuilder<E> sticky() {
                sticky = true;
                return this;
            }

            @Override
            public PublishBuilder<E> stickyClearPrevious() {
                clearPrevious = true;
                return sticky();
            }

            @Override
            public AsyncPublishBuilder<E> async() {
                async = true;
                return new Async();
            }

            @Override
            public AsyncPublishBuilder<E> delayed(long millis) {
                async = true;
                delay = millis;
                return new Async();
            }

            @Override
            public void publish() {
                publishWithSpec(PublishSpec.this);
            }
        }
    }

    public <E> Subscription addInvoker(Class<E> eventClass, final HandlerInvoker<E> invoker) {
        final List<HandlerInvoker> invokers = getInvokers(eventClass);
        invokers.add(invoker);
        for (E event : getStuckEvents(eventClass)) {
            invoker.invoke(event);
        }

        return () -> invokers.remove(invoker);
    }

    private <E> List<E> getStuckEvents(Class<E> eventClass) {
        //noinspection unchecked
        return (List<E>)stuckEventsMap.getOrPut(eventClass);
    }

    private <E> List<HandlerInvoker> getInvokers(Class<E> eventClass) {
        return invokerMap.getOrPut(eventClass);
    }

    private <E> Class<E> getEventClass(E event) {
        //noinspection unchecked
        return (Class<E>)event.getClass();
    }

    public SlimEventBus(SubscriberResolver... resolvers) {
        this.resolvers = Arrays.asList(resolvers);
    }

    @Override
    public <E> void publish(E event) {
        Class eventClass = getEventClass(event);
        while (eventClass != Object.class) {
            Iterable<HandlerInvoker> invokers = invokerMap.get(eventClass);
            for (HandlerInvoker invoker : invokers) {
                //noinspection unchecked
                invoker.invoke(event);
            }
            eventClass = eventClass.getSuperclass();
        }
    }

    @Override
    public <E> PublishBuilder<E> publishBuilder(E event) {
        return new PublishSpec<>(event).builder();
    }

    @Override
    public void clearSticky(Class eventClass) {
        //noinspection unchecked
        getStuckEvents(eventClass).clear();
    }

    @Override
    public <S> Subscription subscribe(final S subscriber) {
        //noinspection unchecked
        Class<S> subscriberClass = (Class<S>)subscriber.getClass();
        return subscribeProvider(subscriberClass, () -> subscriber);
    }

    @Override
    public <S> Subscription subscribeProvider(Class<S> subscriberClass, Provider<S> provider) {
        return getSubscriberForProvider(subscriberClass, provider).subscribe();
    }

    private <S> Subscriber getSubscriberForProvider(Class subscriberClass, Provider<S> provider) {
        Subscriber subscriber = EMPTY_SUBSCRIBER;
        while (subscriberClass != Object.class) {
            for (SubscriberResolver resolver : resolvers) {
                //noinspection unchecked
                Subscriber resolvedSubscriber = resolveSubscriber(resolver, subscriberClass, provider);
                subscriber = chainSubscriber(subscriber, resolvedSubscriber);
            }
            subscriberClass = subscriberClass.getSuperclass();
        }
        return subscriber;
    }

    private Subscriber chainSubscriber(Subscriber first, Subscriber second) {
        if (first == EMPTY_SUBSCRIBER) {
            return second;
        }

        if (second == EMPTY_SUBSCRIBER) {
            return first;
        }

        return () -> {
            Subscription firstSubscription = first.subscribe();
            Subscription secondSubscription = second.subscribe();
            return () -> {
                firstSubscription.unsubscribe();
                secondSubscription.unsubscribe();
            };
        };
    }

    class PublishRunnable<E> implements Runnable {
        private final PublishSpec<E> spec;

        PublishRunnable(PublishSpec<E> spec) {
            this.spec = spec;
        }

        @Override
        public void run() {
            try {
                if (spec.sticky) publishSticky(spec.event, spec.clearPrevious);
                else publish(spec.event);

                if (spec.deliveryCallback != null) {
                    spec.deliveryCallback.onDelivered(spec.event);
                }
            } catch (Throwable e) {
                if (spec.errorHandler != null) {
                    spec.errorHandler.onError(spec.event, e);
                }
            }
        }
    }

    private <E> void publishSticky(E event, boolean clearPrevious) {
        List<E> stuckEvents = getStuckEvents(getEventClass(event));
        if (clearPrevious) stuckEvents.clear();
        stuckEvents.add(event);
        publish(event);
    }

    private <E> void publishWithSpec(final PublishSpec<E> spec) {
        Runnable runnable = new PublishRunnable<>(spec);

        if (spec.async) {
            if (spec.delay > 0) handler.postDelayed(runnable, spec.delay);
            else handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    private <S> Subscriber resolveSubscriber(SubscriberResolver resolver, Class<S> subscriberClass, Provider<S> provider) {
        ClassSubscriber<S> classSubscriber = resolver.resovle(subscriberClass);
        if (classSubscriber != null) {
            return new ObjectSubscriber<>(this, provider, classSubscriber);
        }
        return EMPTY_SUBSCRIBER;
    }

    /**
     * Created by Denis on 25/09/2015.
     *
     */
    static class ObjectSubscriber<S> implements Subscriber {
        private final HandlerInvokerRegistrar registrar;
        private final Provider<S> provider;
        private final ClassSubscriber<S> classSubscriber;

        ObjectSubscriber(HandlerInvokerRegistrar registrar, Provider<S> provider, ClassSubscriber<S> classSubscriber) {
            this.registrar = registrar;
            this.provider = provider;
            this.classSubscriber = classSubscriber;
        }

        public Subscription subscribe() {
            final Subscription[] subscriptions = classSubscriber.subscribe(registrar, provider);

            return () -> {
                for (Subscription subscription : subscriptions) {
                    subscription.unsubscribe();
                }
            };
        }
    }
}
