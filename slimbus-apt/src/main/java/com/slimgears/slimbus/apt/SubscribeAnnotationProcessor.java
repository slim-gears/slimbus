package com.slimgears.slimbus.apt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimbus.Subscribe;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 25/09/2015.
 *
 */
@SupportedAnnotationTypes("com.slimgears.slimbus.Subscribe")
public class SubscribeAnnotationProcessor extends AbstractProcessor {
    class ClassSubscriberGeneratorFactory extends CacheLoader<TypeElement, ClassSubscriberGenerator> {
        @Override
        public ClassSubscriberGenerator load(TypeElement key) {
            try {
                return new ClassSubscriberGenerator(processingEnv, key);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Starting annotation processing...");

        LoadingCache<TypeElement, ClassSubscriberGenerator> classGenerators = CacheBuilder
                .newBuilder()
                .build(new ClassSubscriberGeneratorFactory());

        for (Element element : roundEnv.getElementsAnnotatedWith(Subscribe.class)) {
            processMethod((ExecutableElement)element, classGenerators);
        }

        if (classGenerators.size() > 0) {
            try {
                for (ClassSubscriberGenerator generator : classGenerators.asMap().values()) {
                    System.out.println("Generating " + generator.getTypeName().simpleName());
                    generator.build();
                }

                SubscriberResolverGenerator resolverGenerator = new SubscriberResolverGenerator(processingEnv, classGenerators.asMap().values());
                resolverGenerator.build();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    private void processMethod(ExecutableElement element, LoadingCache<TypeElement, ClassSubscriberGenerator> classGenerators) {
        try {
            TypeElement classElement = (TypeElement)element.getEnclosingElement();
            classGenerators.get(classElement).addHandler(element);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
