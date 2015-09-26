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
    private final ListMap<Class, HandlerInvoker> invokerMap = new ListMap<>();
    private final ListMap<Class, Object> stickedEventsMap = new ListMap<>();
    private final Handler handler = new Handler();
    private final List<SubscriberResolver> resolvers;

    public <E> Unsubscriber addInvoker(Class<E> eventClass, final HandlerInvoker<E> invoker) {
        final List<HandlerInvoker> invokers = getInvokers(eventClass);
        invokers.add(invoker);
        for (E event : getStickedEvents(eventClass)) {
            invoker.invoke(event);
        }
        return () -> invokers.remove(invoker);
    }

    private <E> List<E> getStickedEvents(Class<E> eventClass) {
        //noinspection unchecked
        return (List<E>)stickedEventsMap.getOrPut(eventClass);
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
        //noinspection unchecked
        for (HandlerInvoker<E> invoker : getInvokers(getEventClass(event))) {
            invoker.invoke(event);
        }
    }

    @Override
    public <E> void publishAsync(E event, PublishCallback<E> callback) {
        handler.post(() -> {
            publish(event);
            callback.onDelivered(event);
        });
    }

    @Override
    public <E> void publishSticky(E event) {
        publish(event);
        getStickedEvents(getEventClass(event)).add(event);
    }

    @Override
    public <E> void publishSingleSticky(E event) {
        publish(event);
        List<E> stickedEvents = getStickedEvents(getEventClass(event));
        stickedEvents.clear();
        stickedEvents.add(event);
    }

    @Override
    public <E> void publishDelayed(E event, long delayMilliseconds) {
        handler.postDelayed(() -> publish(event), delayMilliseconds);
    }

    @Override
    public <E> void publishDelayed(E event, long delayMilliseconds, PublishCallback<E> callback) {
        handler.postDelayed(() -> {
            publish(event);
            callback.onDelivered(event);
        }, delayMilliseconds);
    }

    @Override
    public <S> Unsubscriber subscribe(S subscriber) {
        //noinspection unchecked
        Class<S> subscriberClass = (Class<S>)subscriber.getClass();
        return subscribeProvider(subscriberClass, () -> subscriber);
    }

    @Override
    public <S> Unsubscriber subscribeProvider(Class<S> subscriberClass, Provider<S> provider) {
        return getSubscriberForProvider(subscriberClass, provider).subscribe();
    }

    private <S> Subscriber getSubscriberForProvider(Class<S> subscriberClass, Provider<S> provider) {
        for (SubscriberResolver resolver : resolvers) {
            Subscriber subscriber = resolveSubscriber(resolver, subscriberClass, provider);
            if (subscriber != null) return subscriber;
        }
        throw new RuntimeException("Could not resolve subscriber. Please use EventBus.addResolver()");
    }

    private <S> Subscriber resolveSubscriber(SubscriberResolver resolver, Class<S> subscriberClass, Provider<S> provider) {
        ClassSubscriber<S> classSubscriber = resolver.resovle(subscriberClass);
        if (classSubscriber != null) {
            return new ObjectSubscriber<>(this, provider, classSubscriber);
        }
        return null;
    }

    /**
     * Created by Denis on 25/09/2015.
     *
     */
    static class ObjectSubscriber<S> implements Subscriber {
        private final HandlerInvokerRegistrar registrar;
        private final Provider<S> provider;
        private final ClassSubscriber<S> classSubscriber;

        public ObjectSubscriber(HandlerInvokerRegistrar registrar, Provider<S> provider, ClassSubscriber<S> classSubscriber) {
            this.registrar = registrar;
            this.provider = provider;
            this.classSubscriber = classSubscriber;
        }

        public Unsubscriber subscribe() {
            final Unsubscriber[] unsubscribers = classSubscriber.subscribe(registrar, provider);

            return () -> {
                for (Unsubscriber unsubscriber : unsubscribers) {
                    unsubscriber.unsubscribe();
                }
            };
        }
    }
}
