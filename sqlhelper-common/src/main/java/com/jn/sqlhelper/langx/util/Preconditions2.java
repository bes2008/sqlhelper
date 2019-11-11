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

package com.jn.sqlhelper.langx.util;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Predicate;

public class Preconditions2 {

    public static <T> T test(@NonNull Predicate<T> predicate, @Nullable T argument) {
        return test(predicate, argument);
    }

    public static <T> T test(@NonNull Predicate<T> predicate, @Nullable T argument, String message) {
        if (predicate.test(argument)) {
            return argument;
        }
        if (Strings.isNotEmpty(message)) {
            throw new IllegalArgumentException(message);
        } else {
            throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal argument: {}", argument));
        }
    }
}
