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

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class Stringxx {
    static final List<String> vowelLetters = Collects.asList("a", "e", "i", "o", "u");

    public static boolean isVowelLetter(final char c) {
        return Collects.anyMatch(vowelLetters, new Predicate<String>() {
            @Override
            public boolean test(String vowelLetter) {
                return vowelLetter.equalsIgnoreCase("" + c);
            }
        });
    }

    public static boolean startsWithVowelLetter(String string) {
        if (Strings.isBlank(string)) {
            return false;
        }
        return isVowelLetter(string.trim().charAt(0));
    }
}
