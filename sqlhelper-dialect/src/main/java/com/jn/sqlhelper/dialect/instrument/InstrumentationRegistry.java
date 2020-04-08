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

import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.iter.IteratorIterable;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;
import java.util.ServiceLoader;

@Singleton
public class InstrumentationRegistry implements Initializable {
    private static final InstrumentationRegistry instance = new InstrumentationRegistry();
    private List<Instrumentation> instrumentations = Collects.emptyArrayList();
    private boolean inited = false;

    private InstrumentationRegistry() {
        init();
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            this.inited = true;
            ServiceLoader<Instrumentation> loader = ServiceLoader.load(Instrumentation.class);
            Collects.forEach(new IteratorIterable<Instrumentation>(loader.iterator()), new Consumer<Instrumentation>() {
                @Override
                public void accept(Instrumentation instrumentation) {
                    instrumentations.add(instrumentation);
                    instrumentation.init();
                }
            });
        }
    }

    public static InstrumentationRegistry getInstance() {
        return instance;
    }

    public Instrumentation findInstrumentation() {
        return Collects.findFirst(instrumentations, new Predicate<Instrumentation>() {
            @Override
            public boolean test(Instrumentation instrumentation) {
                return instrumentation.isEnabled();
            }
        });
    }

}
