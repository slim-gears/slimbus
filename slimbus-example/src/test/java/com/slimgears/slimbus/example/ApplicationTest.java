package com.slimgears.slimbus.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 16, constants = BuildConfig.class)
public class ApplicationTest {
    private ActivityController<MainActivity> mainActivityController;
    private App app;

    @Before
    public void setUp() {
        Assert.assertTrue(RuntimeEnvironment.application instanceof App);
        app = (App)RuntimeEnvironment.application;
        mainActivityController = Robolectric.buildActivity(MainActivity.class).create();
    }

    @Test
    public void testActivityLifeCycle() {
        mainActivityController
                .start()
                .resume()
                .visible()
                .pause()
                .stop()
                .destroy();
    }

    @Test
    public void testNotifications() {
        app.bus().publish(new NotificationEvent("Test notification"){});
        Assert.assertEquals("Test notification", ShadowToast.getTextOfLatestToast());
    }
}
