package com.slimgears.slimbus.core.internal;

import android.os.Handler;

import com.annimon.stream.Stream;
import com.slimgears.slimbus.core.interfaces.EventBus;
import com.slimgears.slimbus.core.utilities.ListMap;

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

    public static abstract class AbstractObjectSubscriber implements Subscriber {
        private final HandlerInvokerRegistrar registrar;

        protected AbstractObjectSubscriber(HandlerInvokerRegistrar registrar) {
            this.registrar = registrar;
        }

        protected abstract Unsubscriber[] addInvokers(HandlerInvokerRegistrar registrar);

        public Unsubscriber subscribe() {
            final Unsubscriber[] unsubscribers = addInvokers(registrar);
            return () -> Stream.of(unsubscribers).forEach(Unsubscriber::unsubscribe);
        }
    }

    public <E> Unsubscriber addInvoker(Class<E> eventClass, final HandlerInvoker<E> invoker) {
        final List<HandlerInvoker> invokers = getInvokers(eventClass);
        invokers.add(invoker);
        Stream.of(getStickedEvents(eventClass))
                .forEach(invoker::invoke);
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
        Stream.of(getInvokers(getEventClass(event)))
                .forEach(i -> i.invoke(event));
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
        //noinspection unchecked
        return Stream.of(resolvers)
                .map(r -> r.resovle(this, subscriberClass, provider))
                .filter(s -> s != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not resolve subscriber. Please use EventBus.addResolver()"));
    }
}
