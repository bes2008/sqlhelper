package com.jn.sqlhelper.datasource.key.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface DataSourceKeyAnnotationParser<A extends Annotation> extends DataSourceKeyParser<AnnotatedElement> {
    Class<A> getAnnotation();
}
