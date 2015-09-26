package sample.input;

import com.slimgears.slimbus.Subscribe;

class SampleInput {
    @Subscribe
    public void onSampleInputDummyEvent(DummyEvent event) {

    }

    @Subscribe
    public void onSampleInputDummyEventSecond(DummyEvent event) {

    }
}
