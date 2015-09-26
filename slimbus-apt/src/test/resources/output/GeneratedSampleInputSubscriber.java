package sample.input;

import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.internal.ClassSubscriber;
import com.slimgears.slimbus.internal.HandlerInvoker;
import com.slimgears.slimbus.internal.HandlerInvokerRegistrar;
import java.lang.Class;
import java.lang.Override;

public class GeneratedSampleInputSubscriber implements ClassSubscriber<SampleInput> {
    public static final Class<SampleInput> SUBSCRIBER_CLASS = SampleInput.class;

    @Override
    public EventBus.Unsubscriber[] subscribe(HandlerInvokerRegistrar registrar, final EventBus.Provider<SampleInput> provider) {
        return new EventBus.Unsubscriber[] {
                registrar.addInvoker(DummyEvent.class, new HandlerInvoker<DummyEvent>() {
                    @Override
                    public void invoke(DummyEvent event) {
                        provider.provide().onSampleInputDummyEvent(event);
                    }
                }),
                registrar.addInvoker(DummyEvent.class, new HandlerInvoker<DummyEvent>() {
                    @Override
                    public void invoke(DummyEvent event) {
                        provider.provide().onSampleInputDummyEventSecond(event);
                    }
                }),
        }
    }
}
