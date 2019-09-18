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

package com.jn.sqlhelper.dialect.symbolmapper;

import com.jn.langx.util.Chars;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.SqlSymbolMapper;

import java.util.List;

/**
 * {prefix}segment0{underline}segment1{underline}segment2{suffix}
 */
public class CamelToUnderlineSymbolMapper implements SqlSymbolMapper {

    private boolean uppercase = true;
    private String prefix;
    private String suffix;

    public CamelToUnderlineSymbolMapper() {
        this(false);
    }

    public CamelToUnderlineSymbolMapper(boolean uppercase) {
        this(uppercase, null);
    }

    public CamelToUnderlineSymbolMapper(String prefix, boolean uppercase) {
        this(uppercase, prefix, null);
    }

    public CamelToUnderlineSymbolMapper(boolean uppercase, String suffix) {
        this(uppercase, null, suffix);
    }

    public CamelToUnderlineSymbolMapper(boolean uppercase, String prefix, String suffix) {
        this.uppercase = uppercase;
        this.prefix = Strings.isEmpty(prefix) ? "" : prefix;
        this.suffix = Strings.isEmpty(suffix) ? "" : suffix;
    }

    @Override
    public String apply(String name) {
        Preconditions.checkNotEmpty(name);
        // step1: split to segments
        int startIndex = 0;
        List<String> segments = Collects.emptyArrayList();
        boolean lastCharIsUnderline = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                if (startIndex > -1 && i > 0) {
                    segments.add(name.substring(startIndex, i));
                }
                lastCharIsUnderline = true;
                startIndex = -1;
            } else {
                if (lastCharIsUnderline) {
                    startIndex = i;
                } else {
                    if (Chars.isUpperCase(c)) {
                        if (startIndex > -1 && i > 0) {
                            segments.add(name.substring(startIndex, i));
                        }
                        startIndex = i;
                    }
                }
                lastCharIsUnderline = false;
            }
        }
        if (startIndex > -1) {
            segments.add(name.substring(startIndex));
        }

        // step2: concat
        final StringBuilder stringBuilder = new StringBuilder(name.length() + 20);
        stringBuilder.append(prefix);
        for (int i = 0; i < segments.size(); i++) {
            if (i > 0) {
                stringBuilder.append("_");
            }
            stringBuilder.append(segments.get(i));
        }
        stringBuilder.append(suffix);

        return uppercase ? stringBuilder.toString().toUpperCase() : stringBuilder.toString().toLowerCase();
    }
}
