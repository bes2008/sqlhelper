/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objects;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.iter.IteratorIterable;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;

@Singleton
public class InstrumentationRegistry implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentationRegistry.class);
    private static final InstrumentationRegistry instance = new InstrumentationRegistry();
    /**
     * key: instrumentation's class full name
     */
    private Map<String, Instrumentation> instrumentationMap = Collects.emptyHashMap();
    private Map<String, String> aliasMap = Collects.emptyHashMap();
    private boolean inited = false;

    private InstrumentationRegistry() {
        init();
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            this.inited = true;
            ServiceLoader<Instrumentation> loader = ServiceLoader.load(Instrumentation.class);
            Collects.forEach(loader, new Consumer<Instrumentation>() {
                @Override
                public void accept(Instrumentation instrumentation) {
                    String alias = Instrumentations.getAliasName(instrumentation);
                    String classFullName = Reflects.getFQNClassName(instrumentation.getClass());
                    if (Objects.isNotEmpty(alias)) {
                        aliasMap.put(alias, classFullName);
                    }
                    instrumentationMap.put(classFullName, instrumentation);
                    instrumentation.init();
                }
            });
        }
    }

    public static InstrumentationRegistry getInstance() {
        return instance;
    }

    public void enableInstrumentation(@NonNull String name) {
        if (Objects.isNotEmpty(name)) {
            logger.info("Start to enable SQL instrumentation: {}", name);
            Instrumentation instrumentation = findInstrumentation(name, false);
            if (instrumentation != null) {
                instrumentation.setEnabled(true);
            }
        }
    }

    public Instrumentation findInstrumentation(@Nullable String name) {
        return findInstrumentation(name, true);
    }

    private Instrumentation findInstrumentation(@Nullable String name, final boolean enabled) {
        if (name == null) {
            return Collects.findFirst(instrumentationMap.values(), new Predicate<Instrumentation>() {
                @Override
                public boolean test(Instrumentation instrumentation) {
                    return !enabled || instrumentation.isEnabled();
                }
            });
        } else {
            String className = aliasMap.get(name);
            if (className == null) {
                className = name;
            }
            Instrumentation instrumentation = instrumentationMap.get(className);
            if (instrumentation == null) {
                return null;
            }
            if (enabled && !instrumentation.isEnabled()) {
                return null;
            }
            return instrumentation;
        }
    }


}
