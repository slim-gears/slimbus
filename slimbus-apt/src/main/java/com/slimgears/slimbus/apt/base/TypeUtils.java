package com.slimgears.slimbus.apt.base;

import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;

/**
 * Created by Denis on 08-May-15.
 */
public class TypeUtils {
    public static String packageName(String qualifiedClassName) {
        int pos = qualifiedClassName.lastIndexOf('.');
        return (pos >= 0) ? qualifiedClassName.substring(0, pos) : "";
    }

    public static String simpleName(String qualifiedClassName) {
        String packageName = packageName(qualifiedClassName);
        return packageName.isEmpty() ? qualifiedClassName : qualifiedClassName.substring(packageName.length() + 1);
    }

    public static TypeName getTypeName(TypeElement element) {
        return TypeName.get(element.asType());
    }

    public interface AnnotationTypesGetter<TAnnotation extends Annotation> {
        Class[] getTypes(TAnnotation annotation) throws MirroredTypesException;
    }

    public interface AnnotationTypeGetter<TAnnotation extends Annotation> {
        Class getType(TAnnotation annotation) throws MirroredTypeException;
    }
}
