package com.slimgears.slimbus.apt;

import com.slimgears.slimapt.AnnotationProcessingTestBase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Denis on 25/09/2015.
 *
 */
@RunWith(JUnit4.class)
public class SubscribeAnnotationProcessorTest extends AnnotationProcessingTestBase {
    @Test
    public void testClassSubscriberGeneration() {
        testAnnotationProcessing(
                processedWith(new SubscribeAnnotationProcessor()),
                inputFiles("SampleInput.java", "DummyEvent.java", "DummyContainer.java"),
                expectedFiles("GeneratedSampleInputSubscriber.java", "GeneratedDummyContainer_DummyBusFactory.java"));
    }
}
