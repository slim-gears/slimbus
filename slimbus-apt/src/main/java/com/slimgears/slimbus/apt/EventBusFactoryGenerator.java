package com.slimgears.slimbus.apt;

import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimbus.BusFactory;
import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.internal.AbstractSubscriberResolver;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 25/09/2015.
 *
 */
public class EventBusFactoryGenerator extends ClassGenerator<EventBusFactoryGenerator> {
    private final Collection<ClassSubscriberGenerator> classSubscriberGenerators;
    private final TypeName busType;

    public EventBusFactoryGenerator(ProcessingEnvironment processingEnvironment, TypeElement baseInterface, Collection<ClassSubscriberGenerator> classSubscriberGenerators) {
        super(processingEnvironment);
        this.classSubscriberGenerators = classSubscriberGenerators;

        BusFactory annotation = baseInterface.getAnnotation(BusFactory.class);
        busType = TypeUtils.getTypeFromAnnotation(annotation, BusFactory::busClass);

        String qualifiedName = TypeUtils.qualifiedName(baseInterface);
        String packageName = TypeUtils.packageName(qualifiedName);
        String simpleName = "Generated" + TypeUtils.simpleName(qualifiedName).replace('$', '_');

        this
            .className(packageName, simpleName)
            .superClass(AbstractSubscriberResolver.class)
            .addInterfaces(baseInterface);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        builder
                .addModifiers(Modifier.PUBLIC)
                .addField(
                        FieldSpec
                                .builder(
                                        ClassName.get(interfaces[0]),
                                        "INSTANCE",
                                        Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                                .initializer("new $T()", getTypeName())
                                .build());

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        for (ClassSubscriberGenerator generator : classSubscriberGenerators) {
            constructorBuilder.addCode("addSubscriber($T.SUBSCRIBER_CLASS, new $T());\n", generator.getTypeName(), generator.getTypeName());
        }

        builder.addMethod(constructorBuilder.build());
        builder.addMethod(MethodSpec
                .methodBuilder("createEventBus")
                .addAnnotation(Override.class)
                .returns(EventBus.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode("return new $T(this);\n", busType)
                .build());
    }
}
