package com.slimgears.slimbus.apt;

import com.slimgears.slimbus.apt.base.ClassGenerator;
import com.slimgears.slimbus.apt.base.ElementVisitorBase;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Denis on 24/09/2015.
 */
public class SubscriberInvokerGenerator extends ClassGenerator<SubscriberInvokerGenerator> {
    protected SubscriberInvokerGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    class Visitor extends ElementVisitorBase<Void, Void> {
        private final TypeSpec.Builder builder;
        private final MethodSpec.Builder modelCtorBuilder;
        private final MethodSpec.Builder ctorBuilder;

        Visitor(TypeSpec.Builder builder, MethodSpec.Builder modelCtorBuilder, MethodSpec.Builder ctorBuilder) {
            this.builder = builder;
            this.modelCtorBuilder = modelCtorBuilder;
            this.ctorBuilder = ctorBuilder;
        }

        @Override
        public Void visitExecutable(ExecutableElement method, Void param) {
            TypeMirror returnType = method.getReturnType();
            return null;
        }
    }


    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {

    }
}
