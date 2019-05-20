
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

package com.github.fangjinuo.sqlhelper.util;

import java.util.Iterator;

public class Strings {
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String truncate(final String string, final int length) {
        if (string.length() <= length) {
            return string;
        }
        return string.substring(0, length);
    }

    public static String join(final String seperator, final Iterator objects) {
        final StringBuilder buf = new StringBuilder();
        if (objects.hasNext()) {
            buf.append(objects.next());
        }
        while (objects.hasNext()) {
            buf.append(seperator).append(objects.next());
        }
        return buf.toString();
    }

}
