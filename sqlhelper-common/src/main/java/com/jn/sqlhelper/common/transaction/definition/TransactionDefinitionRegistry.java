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

package com.jn.sqlhelper.common.transaction.definition;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.common.transaction.definition.parser.NamedTransactionDefinitionParser;
import com.jn.sqlhelper.common.transaction.definition.parser.TransactionDefinitionAnnotationParser;
import com.jn.sqlhelper.common.transaction.definition.parser.TransactionalAnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionDefinitionRegistry implements Registry<Method, TransactionDefinition> {
    /**
     * 这里的 DataSourceKey，可以是一个确切的 key，也可以是个 keyPattern。
     * 不论是确切的key，还是个keyPattern，在DataSourceRegister里进行pattern匹配的。
     */
    private final ConcurrentHashMap<Method, Holder<TransactionDefinition>> methodDataSourceKeyCache = new ConcurrentHashMap<Method, Holder<TransactionDefinition>>();

    /**
     * parser 缓存
     */
    private Map<Class<? extends Annotation>, TransactionDefinitionAnnotationParser> annotationParserMap = new LinkedHashMap<Class<? extends Annotation>, TransactionDefinitionAnnotationParser>();

    private Map<String, NamedTransactionDefinitionParser> namedTransactionDefinitionParserMap = new LinkedHashMap<String, NamedTransactionDefinitionParser>();


    public TransactionDefinitionRegistry() {
        registerTransactionAnnotationParser(new TransactionalAnnotationParser());
    }

    @Override
    public void register(TransactionDefinition dataSourceKeyHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(Method method, TransactionDefinition dataSourceKey) {
        Preconditions.checkNotNull(dataSourceKey);
        methodDataSourceKeyCache.putIfAbsent(method, new Holder<TransactionDefinition>(dataSourceKey));
    }

    public void registerTransactionAnnotationParser(TransactionDefinitionAnnotationParser transactionDefinitionAnnotationParser) {
        if (transactionDefinitionAnnotationParser != null && transactionDefinitionAnnotationParser.getAnnotation() != null) {
            annotationParserMap.put(transactionDefinitionAnnotationParser.getAnnotation(), transactionDefinitionAnnotationParser);
        }
    }

    public void registerNamedTransactionParser(NamedTransactionDefinitionParser transactionDefinitionParser) {
        namedTransactionDefinitionParserMap.put(transactionDefinitionParser.getName(), transactionDefinitionParser);
    }

    @Override
    @NonNull
    public TransactionDefinition get(final Method method) {
        Holder<TransactionDefinition> holder = methodDataSourceKeyCache.get(method);
        if (holder == null) {
            // 第一次用到该方法
            synchronized (this) {
                final Holder<TransactionDefinition> holder0 = new Holder<TransactionDefinition>();

                // 优先使用注解
                Collects.forEach(annotationParserMap, new Consumer2<Class<? extends Annotation>, TransactionDefinitionAnnotationParser>() {
                    @Override
                    public void accept(Class<? extends Annotation> annotationClass, TransactionDefinitionAnnotationParser parser) {
                        TransactionDefinition key = parser.parse(method);
                        if (key != null) {
                            holder0.set(key);
                        }
                    }
                }, new Predicate2<Class<? extends Annotation>, TransactionDefinitionAnnotationParser>() {
                    @Override
                    public boolean test(Class<? extends Annotation> key, TransactionDefinitionAnnotationParser value) {
                        return !holder0.isNull();
                    }
                });

                // 再用 named transaction parser
                Collects.forEach(namedTransactionDefinitionParserMap, new Consumer2<String, NamedTransactionDefinitionParser>() {
                    @Override
                    public void accept(String parserName, NamedTransactionDefinitionParser parser) {
                        TransactionDefinition key = parser.parse(method);
                        if (key != null) {
                            holder0.set(key);
                        }
                    }
                }, new Predicate2<String, NamedTransactionDefinitionParser>() {
                    @Override
                    public boolean test(String parserName, NamedTransactionDefinitionParser value) {
                        return !holder0.isNull();
                    }
                });

                methodDataSourceKeyCache.putIfAbsent(method, holder0);
                holder = methodDataSourceKeyCache.get(method);
            }
        }
        return holder.get();
    }
}
