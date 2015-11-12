package sample.input;

import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.apt.DummyEventBus;
import com.slimgears.slimbus.internal.AbstractSubscriberResolver;
import java.lang.Override;

public class GeneratedDummyContainer_DummyBusFactory extends AbstractSubscriberResolver implements DummyContainer.DummyBusFactory {
    public static final DummyContainer.DummyBusFactory INSTANCE = new GeneratedDummyContainer_DummyBusFactory();

    private GeneratedDummyContainer_DummyBusFactory() {
        addSubscriber(GeneratedSampleInputSubscriber.SUBSCRIBER_CLASS, new GeneratedSampleInputSubscriber());
    }

    @Override
    public EventBus createEventBus() {
        return new DummyEventBus(this);
    }
}
