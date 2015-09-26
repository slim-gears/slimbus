package com.slimgears.slimbus.apt;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaFileObject;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

/**
 * Created by Denis on 25/09/2015.
 *
 */
@RunWith(JUnit4.class)
public class SubscribeAnnotationProcessorTest {
    private static Iterable<JavaFileObject> inputFiles(String... files) {
        return fromResources("input", files);
    }

    private static Iterable<JavaFileObject> expectedFiles(String... files) {
        return fromResources("output", files);
    }

    private static Iterable<JavaFileObject> fromResources(final String path, String[] files) {
        return transform(asList(files), new Function<String, JavaFileObject>() {
            @Override
            public JavaFileObject apply(String input) {
                return JavaFileObjects.forResource(path + '/' + input);
            }
        });
    }

    private void testAnnotationProcessing(AbstractProcessor processor, Iterable<JavaFileObject> inputs, Iterable<JavaFileObject> expectedOutputs) {
        assert_()
                .about(javaSources())
                .that(inputs)
                .processedWith(processor)
                .compilesWithoutError()
                .and().generatesSources(Iterables.getFirst(expectedOutputs, null), toArray(skip(expectedOutputs, 1), JavaFileObject.class));
    }

    @Test
    public void testClassSubscriberGeneration() {
        testAnnotationProcessing(
                new SubscribeAnnotationProcessor(),
                inputFiles("SampleInput.java", "DummyEvent.java"),
                expectedFiles("GeneratedSampleInputSubscriber.java", "GeneratedSubscriberResolver.java"));
    }
}
