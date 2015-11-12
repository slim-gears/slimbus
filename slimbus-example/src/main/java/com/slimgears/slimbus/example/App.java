package com.slimgears.slimbus.example;

import android.app.Application;

import com.slimgears.slimbus.BusFactory;
import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.EventBusFactory;
import com.slimgears.slimbus.SlimEventBus;

/**
 * Created by Denis on 26/09/2015.
 *
 */
public class App extends Application {

    private EventBus bus;

    public EventBus bus() {
        return bus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = GeneratedApp_SlimEventBusFactory.INSTANCE.createEventBus();
        bus.subscribeProvider(ToasterNotifier.class, new EventBus.Provider<ToasterNotifier>() {
            @Override
            public ToasterNotifier provide() {
                return new ToasterNotifier(App.this);
            }
        });
    }

    /**
     * Created by ditskovi on 11/11/2015.
     *
     */
    @BusFactory(busClass = SlimEventBus.class)
    interface SlimEventBusFactory extends EventBusFactory {
    }
}
