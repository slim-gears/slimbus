package com.slimgears.slimbus.apt;

import com.slimgears.slimbus.EventBus;
import com.slimgears.slimbus.apt.base.ClassGenerator;
import com.slimgears.slimbus.apt.base.TypeUtils;
import com.slimgears.slimbus.internal.ClassSubscriber;
import com.slimgears.slimbus.internal.HandlerInvoker;
import com.slimgears.slimbus.internal.HandlerInvokerRegistrar;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Denis on 25/09/2015.
 *
 */
class ClassSubscriberGenerator extends ClassGenerator<ClassSubscriberGenerator> {
    class HandlerInfo {
        String methodName;
        TypeName eventType;

        TypeName invokerType() {
            return ParameterizedTypeName.get(
                    ClassName.get(HandlerInvoker.class),
                    eventType);
        }
    }

    private final List<HandlerInfo> handlers = new ArrayList<>();
    private final TypeName subscriberTypeName;

    public ClassSubscriberGenerator(ProcessingEnvironment processingEnv, TypeElement subscriberClass) {
        super(processingEnv);
        subscriberTypeName = TypeUtils.getTypeName(subscriberClass);

        String packageName = TypeUtils.packageName(subscriberClass.getQualifiedName().toString());
        String className = subscriberClass.getSimpleName().toString();

        this
                .className(packageName, "Generated" + className + "Subscriber")
                .superClass(Object.class)
                .addInterfaces(ParameterizedTypeName.get(
                        ClassName.get(ClassSubscriber.class),
                        subscriberTypeName));
    }

    public void addHandler(ExecutableElement element) {
        HandlerInfo info = new HandlerInfo();
        info.methodName = element.getSimpleName().toString();
        List<? extends VariableElement> params = element.getParameters();

        if (params.size() != 1) {
            throw new RuntimeException("Event handler method should receive exactly 1 parameter");
        }

        info.eventType = TypeName.get(params.get(0).asType());
        handlers.add(info);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        MethodSpec.Builder subscribeMethodBuilder = MethodSpec
                .methodBuilder("subscribe")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayTypeName.of(EventBus.Subscription.class))
                .addParameter(HandlerInvokerRegistrar.class, "registrar")
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(EventBus.Provider.class), subscriberTypeName),
                        "provider",
                        Modifier.FINAL)
                .addCode("return new $T[] {\n", EventBus.Subscription.class);

        for (HandlerInfo handler : handlers) {
            subscribeMethodBuilder
                    .addCode("    $L.addInvoker($T.class, new $T() {\n", "registrar", handler.eventType, handler.invokerType())
                    .addCode("        @$T public void invoke($T $L) {\n", Override.class, handler.eventType, "event")
                    .addCode("            $L.provide().$L($L);\n", "provider", handler.methodName, "event")
                    .addCode("        }\n")
                    .addCode("    }),\n");
        }

        subscribeMethodBuilder.addCode("};\n");
        builder
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec
                        .builder(ParameterizedTypeName.get(ClassName.get(Class.class), subscriberTypeName), "SUBSCRIBER_CLASS", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.class", subscriberTypeName)
                        .build())
                .addMethod(subscribeMethodBuilder.build());
    }
}
