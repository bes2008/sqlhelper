
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

package com.jn.sqlhelper.dialect.urlparser.oracle;

public class Token {
    private final String token;
    private final int type;

    public Token(final String token, final int type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return this.token;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Token");
        sb.append("{token='").append(this.token).append('\'');
        sb.append(", type=").append(this.type);
        sb.append('}');
        return sb.toString();
    }
}
