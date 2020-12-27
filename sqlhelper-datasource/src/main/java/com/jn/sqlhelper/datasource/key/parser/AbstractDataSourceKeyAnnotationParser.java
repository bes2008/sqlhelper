package com.jn.sqlhelper.datasource.key.parser;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public abstract class AbstractDataSourceKeyAnnotationParser<A extends Annotation> implements DataSourceKeyAnnotationParser<A> {
    @Override
    public DataSourceKey parse(AnnotatedElement annotatedElement) {
        Preconditions.checkNotNull(annotatedElement);

        if (!Reflects.hasAnnotation(annotatedElement, getAnnotation())) {
            if (annotatedElement instanceof Method) {
                Method method = (Method) annotatedElement;
                Class clazz = method.getDeclaringClass();
                return parse(clazz);
            }
            return null;
        }
        if (annotatedElement instanceof Method || annotatedElement instanceof Class) {
            A annotation = Reflects.getAnnotation(annotatedElement, getAnnotation());
            return internalParse(annotation);
        }
        return null;
    }

    protected abstract DataSourceKey internalParse(@NonNull A annotation);
}
