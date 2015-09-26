package com.slimgears.slimbus.example;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 16,
        manifest = "build/intermediates/manifests/full/" + BuildConfig.BUILD_TYPE + "/AndroidManifest.xml",
        resourceDir = "../../../res/" + BuildConfig.BUILD_TYPE)
public class ApplicationTest {
    private ActivityController<MainActivity> mainActivityController;
    private App app;

    private static <T extends Activity> T activateActivity(ActivityController<T> controller) {
        return controller.start().resume().visible().get();
    }

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
        app.bus().publish(new NotificationEvent("Test notification"));
        Assert.assertEquals("Test notification", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testRobolectricSanity() throws IOException {
        Assert.assertNotNull(RuntimeEnvironment.application);
    }
}
