/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.common.transaction.definition.parser;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.transaction.TransactionDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public abstract class AbstractTransactionDefinitionAnnotationParser<A extends Annotation> implements TransactionDefinitionAnnotationParser<A> {
    @Override
    public TransactionDefinition parse(AnnotatedElement annotatedElement) {
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
            return internalParse(annotatedElement, annotation);
        }
        return null;
    }

    protected abstract TransactionDefinition internalParse(AnnotatedElement annotatedElement, @NonNull A annotation);
}
