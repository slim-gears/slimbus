package com.slimgears.slimbus.apt;

import com.slimgears.slimbus.apt.base.ClassGenerator;
import com.slimgears.slimbus.internal.AbstractSubscriberResolver;
import com.slimgears.slimbus.internal.SubscriberResolver;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 25/09/2015.
 *
 */
public class SubscriberResolverGenerator extends ClassGenerator<SubscriberResolverGenerator> {
    private final Collection<ClassSubscriberGenerator> classSubscriberGenerators;

    public SubscriberResolverGenerator(ProcessingEnvironment processingEnvironment, Collection<ClassSubscriberGenerator> classSubscriberGenerators) {
        super(processingEnvironment);
        this.classSubscriberGenerators = classSubscriberGenerators;

        this
            .className(calculatePackageName(classSubscriberGenerators), "GeneratedSubscriberResolver")
            .superClass(AbstractSubscriberResolver.class);
    }

    private static String calculatePackageName(Iterable<ClassSubscriberGenerator> generators) {
        String commonPackageName = null;
        for (ClassSubscriberGenerator generator : generators) {
            commonPackageName = getCommonPrefix(generator.getPackageName(), commonPackageName);
        }
        return (commonPackageName == null || commonPackageName.isEmpty()) ? "" : commonPackageName;
    }

    private static String getCommonPrefix(String a, String b) {
        if (b == null) return a;
        else if (a == null) return b;

        int size = Math.min(a.length(), b.length());
        for (int i = 0; i < size; ++i) {
            if (a.charAt(i) != b.charAt(i)) {
                String common = a.substring(0, i);
                if (common.endsWith(".")) return common.substring(0, common.length() - 1);
            }
        }
        return a.substring(0, size);
    }


    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        builder
                .addModifiers(Modifier.PUBLIC)
                .addField(
                        FieldSpec
                                .builder(
                                        ClassName.get(SubscriberResolver.class),
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
    }
}
