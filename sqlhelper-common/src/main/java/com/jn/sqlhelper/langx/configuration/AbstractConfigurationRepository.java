/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.langx.configuration;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Cache;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractConfigurationRepository<T extends Configuration, Loader extends ConfigurationLoader<T>, Writer extends ConfigurationWriter<T>> implements ConfigurationRepository<T, Loader, Writer> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigurationRepository.class);
    @NonNull
    protected String name;
    @NonNull
    protected Loader loader;
    @Nullable
    protected Writer writer;
    protected volatile boolean inited = false;
    protected volatile boolean running = false;
    @Nullable
    private EventPublisher eventPublisher;
    @NonNull
    private Cache<String, T> cache;

    public void setCache(Cache<String, T> cache) {
        this.cache = cache;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    @Override
    public void startup() {
        if (!inited) {
            init();
        }
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(cache);
        running = true;
        logger.info("Startup configuration repository: {}", name);
    }

    @Override
    public void shutdown() {
        running = false;
        logger.info("Shutdown configuration repository: {}", name);
        cache.clean();
    }

    @Override
    public void setConfigurationLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void setConfigurationWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public T getById(String id) {
        return cache.get(id);
    }

    @Override
    public void removeById(String id) {
        removeById(id, true);
    }

    @Override
    public void removeById(String id, boolean sync) {
        cache.remove(id);
        if (sync && writer != null && writer.isSupportsRemove()) {
            writer.remove(id);
        }
    }

    @Override
    public void add(T configuration) {
        add(configuration, true);
    }

    @Override
    public void add(T configuration, boolean sync) {
        if (running) {
            if (sync && writer != null && writer.isSupportsWrite()) {
                writer.write(configuration);
            }
            cache.set(configuration.getId(), configuration);
        }
    }

    @Override
    public void update(T configuration) {
        update(configuration, true);
    }

    @Override
    public void update(T configuration, boolean sync) {
        if (running) {
            cache.set(configuration.getId(), configuration);
            if (sync && writer != null && writer.isSupportsRewrite()) {
                writer.rewrite(configuration);
            }
        }
    }

    @Override
    public void init() throws InitializationException {
        logger.info("Initial configuration repository: {}", name);
    }

    public final Map<String, T> getAll() {
        return Collections.unmodifiableMap(cache.toMap());
    }

}
