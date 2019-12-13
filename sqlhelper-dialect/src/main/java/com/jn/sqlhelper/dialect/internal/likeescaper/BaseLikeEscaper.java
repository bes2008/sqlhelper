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

package com.jn.sqlhelper.dialect.internal.likeescaper;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.PrimitiveArrays;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.dialect.LikeEscaper;

import java.util.List;

public class BaseLikeEscaper implements LikeEscaper {

    protected static final List<Character> STANDARD_LIKE_KEY_CHARS = Collects.asList(
            '\'', '_', '%', '\\'
    );

    protected char escapeChar = '0';

    public BaseLikeEscaper() {
    }

    public BaseLikeEscaper(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    @Override
    public List<Character> getLikeKeyChars() {
        return STANDARD_LIKE_KEY_CHARS;
    }

    @Override
    public String escapeLikeKeyChars(String pattern) {
        final List<Character> specifiedChars = getLikeKeyChars();
        if (Emptys.isNotEmpty(pattern)) {
            final StringBuilder builder = new StringBuilder(pattern.length() + 20);
            Collects.forEach(PrimitiveArrays.wrap(pattern.toCharArray(), true), new Consumer<Character>() {
                @Override
                public void accept(Character c) {
                    if (specifiedChars.contains(c)) {
                        builder.append(escapeLikeKeyChar(c));
                    } else {
                        if (c == escapeChar) {
                            builder.append(escapeChar).append(escapeChar);
                        } else {
                            builder.append(c);
                        }
                    }
                }
            });
            return builder.toString();
        }
        return pattern;
    }

    protected String escapeLikeKeyChar(char c) {
        return escapeChar == '0' ? ("" + c) : ("" + escapeChar + c);
    }

    @Override
    public String appendmentAfterLikeClause() {
        return "";
    }
}
