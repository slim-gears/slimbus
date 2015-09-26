package sample.input;

import com.slimgears.slimbus.internal.AbstractSubscriberResolver;
import com.slimgears.slimbus.internal.SubscriberResolver;

public class GeneratedSubscriberResolver extends AbstractSubscriberResolver {
    public static final SubscriberResolver INSTANCE = new GeneratedSubscriberResolver();

    private GeneratedSubscriberResolver() {
        addSubscriber(GeneratedSampleInputSubscriber.SUBSCRIBER_CLASS, new GeneratedSampleInputSubscriber());
    }
}
