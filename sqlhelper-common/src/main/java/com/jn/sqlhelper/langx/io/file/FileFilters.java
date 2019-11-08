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

package com.jn.sqlhelper.langx.io.file;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.file.FileFilter;
import com.jn.langx.util.io.file.filter.AbstractFileFilter;

import java.io.File;
import java.util.List;

public class FileFilters {

    public static FileFilter allFileFilter(@NonNull List<? extends FileFilter> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<FileFilter> pipeline = Pipeline.<FileFilter>of(predicates);
        return new AbstractFileFilter() {
            @Override
            public boolean accept(final File e) {
                return pipeline.allMatch(new Predicate<FileFilter>() {
                    @Override
                    public boolean test(FileFilter fileFilter) {
                        return fileFilter.test(e);
                    }
                });
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }

        };
    }

    public static FileFilter anyFileFilter(@NonNull List<? extends FileFilter> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<FileFilter> pipeline = Pipeline.<FileFilter>of(predicates);
        return new AbstractFileFilter() {
            @Override
            public boolean accept(final File e) {
                return pipeline.anyMatch(new Predicate<FileFilter>() {
                    @Override
                    public boolean test(FileFilter fileFilter) {
                        return fileFilter.test(e);
                    }
                });
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }
        };
    }


    public static <E> Predicate<E> allPredicate(@NonNull List<Predicate<E>> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<Predicate<E>> pipeline = Pipeline.<Predicate<E>>of(predicates);
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return pipeline.allMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> anyPredicate(@NonNull List<Predicate<E>> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<Predicate<E>> pipeline = Pipeline.<Predicate<E>>of(predicates);
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return pipeline.anyMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> andPredicate(@NonNull List<Predicate<E>> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<Predicate<E>> pipeline = Pipeline.<Predicate<E>>of(predicates);
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return pipeline.allMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

    public static <E> Predicate<E> orPredicate(@NonNull List<Predicate<E>> predicates) {
        Preconditions.checkTrue(Emptys.isNotEmpty(predicates));
        final Pipeline<Predicate<E>> pipeline = Pipeline.<Predicate<E>>of(predicates);
        return new Predicate<E>() {
            @Override
            public boolean test(final E value) {
                return pipeline.anyMatch(new Predicate<Predicate<E>>() {
                    @Override
                    public boolean test(Predicate<E> filter) {
                        return filter.test(value);
                    }
                });
            }
        };
    }

}
