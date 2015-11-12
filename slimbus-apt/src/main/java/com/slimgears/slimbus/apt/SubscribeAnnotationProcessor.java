package com.slimgears.slimbus.apt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimbus.BusFactory;
import com.slimgears.slimbus.EventBusFactory;
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
import javax.lang.model.util.Types;

/**
 * Created by Denis on 25/09/2015.
 *
 */
@SupportedAnnotationTypes({"com.slimgears.slimbus.Subscribe", "com.slimgears.slimbus.BusFactory"})
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

                for (Element element : roundEnv.getElementsAnnotatedWith(BusFactory.class)) {
                    TypeElement typeElement = (TypeElement)element;

                    validateBusFactoryElement(typeElement);
                    EventBusFactoryGenerator busGenerator = new EventBusFactoryGenerator(processingEnv, typeElement, classGenerators.asMap().values());
                    busGenerator.build();
                }


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

    private void validateBusFactoryElement(TypeElement typeElement) {
        Types typeUtils = processingEnv.getTypeUtils();
        TypeElement eventBusFactoryElement = processingEnv.getElementUtils().getTypeElement(EventBusFactory.class.getCanonicalName());
        if (!typeUtils.isAssignable(typeElement.asType(), eventBusFactoryElement.asType())) {
            throw new IllegalArgumentException(String.format("%1s does not extend %2s", typeElement.getQualifiedName(), eventBusFactoryElement.getSimpleName()));
        }
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
