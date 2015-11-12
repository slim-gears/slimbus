package sample.input;
import com.slimgears.slimbus.EventBusFactory;
import com.slimgears.slimbus.BusFactory;
import com.slimgears.slimbus.apt.DummyEventBus;

class DummyContainer {
    @BusFactory(busClass = DummyEventBus.class)
    interface DummyBusFactory extends EventBusFactory {
    }
}
