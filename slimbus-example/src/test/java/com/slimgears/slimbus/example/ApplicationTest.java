package com.slimgears.slimbus.example;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, manifest=Config.NONE)
public class ApplicationTest {
    @Test
    public void repositorySanityCheck() throws IOException {
        Assert.assertNotNull(RuntimeEnvironment.application);
    }
}
