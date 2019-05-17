
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

package com.github.fangjinuo.sqlhelper.dialect.internal.urlparser.oracle;

import java.util.ArrayList;
import java.util.List;

public class OracleNetConnectionDescriptorTokenizer {
    public static final char TOKEN_EQUAL = '=';
    public static final char TOKEN_KEY_START = '(';
    public static final char TOKEN_KEY_END = ')';
    private static final char TOKEN_COMMA = ',';
    private static final char TOKEN_BKSLASH = '\\';
    private static final char TOKEN_DQUOTE = '\"';
    private static final char TOKEN_SQUOTE = '\'';
    public static final int TYPE_KEY_START = 0;
    public static final Token TOKEN_KEY_START_OBJECT;
    public static final int TYPE_KEY_END = 1;
    public static final Token TOKEN_KEY_END_OBJECT;
    public static final int TYPE_EQUAL = 2;
    public static final Token TOKEN_EQUAL_OBJECT;
    public static final int TYPE_LITERAL = 3;
    public static final int TYPE_EOF = -1;
    public static final Token TOKEN_EOF_OBJECT;
    private final List<Token> tokenList;
    private int tokenPosition;
    private final String connectionString;
    private int position;

    public OracleNetConnectionDescriptorTokenizer(final String connectionString) {
        this.tokenList = new ArrayList<Token>();
        this.tokenPosition = 0;
        this.position = 0;
        if (connectionString == null) {
            throw new RuntimeException("connectionString");
        }
        this.connectionString = connectionString;
    }

    public void parse() {
        final int length = this.connectionString.length();
        while (this.position < length) {
            final char ch = this.connectionString.charAt(this.position);
            if (!this.isWhiteSpace(ch)) {
                switch (ch) {
                    case '(':
                        this.tokenList.add(OracleNetConnectionDescriptorTokenizer.TOKEN_KEY_START_OBJECT);
                        break;
                    case '=':
                        this.tokenList.add(OracleNetConnectionDescriptorTokenizer.TOKEN_EQUAL_OBJECT);
                        break;
                    case ')':
                        this.tokenList.add(OracleNetConnectionDescriptorTokenizer.TOKEN_KEY_END_OBJECT);
                        break;
                    case '\"':
                    case '\'':
                    case ',':
                    case '\\':
                        throw new RuntimeException("unsupported token:" + ch);
                    default: {
                        final String literal = this.parseLiteral();
                        this.addToken(literal, 3);
                        break;
                    }
                }
            }
            ++this.position;
        }
        this.tokenList.add(OracleNetConnectionDescriptorTokenizer.TOKEN_EOF_OBJECT);
    }

    String parseLiteral() {
        final int start = this.trimLeft();
        this.position = start;
        while (this.position < this.connectionString.length()) {
            final char ch = this.connectionString.charAt(this.position);
            switch (ch) {
                case '(':
                case ')':
                case '=': {
                    final int end = this.trimRight(this.position);
                    --this.position;
                    return this.connectionString.substring(start, end);
                }
                default:
                    ++this.position;
                    continue;
            }
        }
        final int end2 = this.trimRight(this.position);
        return this.connectionString.substring(start, end2);
    }

    int trimRight(final int index) {
        int end;
        for (end = index; end > 0; --end) {
            final char ch = this.connectionString.charAt(end - 1);
            if (!this.isWhiteSpace(ch)) {
                return end;
            }
        }
        return end;
    }

    int trimLeft() {
        int length;
        int start;
        for (length = this.connectionString.length(), start = this.position; start < length; ++start) {
            final char ch = this.connectionString.charAt(start);
            if (!this.isWhiteSpace(ch)) {
                return start;
            }
        }
        return start;
    }

    private void addToken(final String tokenString, final int type) {
        final Token token = new Token(tokenString, type);
        this.tokenList.add(token);
    }

    private boolean isWhiteSpace(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    public Token nextToken() {
        if (this.tokenList.size() <= this.tokenPosition) {
            return null;
        }
        final Token token = (Token) this.tokenList.get(this.tokenPosition);
        ++this.tokenPosition;
        return token;
    }

    public void nextPosition() {
        if (this.tokenList.size() <= this.tokenPosition) {
            return;
        }
        ++this.tokenPosition;
    }

    public Token lookAheadToken() {
        if (this.tokenList.size() <= this.tokenPosition) {
            return null;
        }
        return this.tokenList.get(this.tokenPosition);
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public void checkStartToken() {
        final Token token = this.nextToken();
        if (token == null) {
            throw new RuntimeException("parse error. token is null");
        }
        if (token != OracleNetConnectionDescriptorTokenizer.TOKEN_KEY_START_OBJECT) {
            throw new RuntimeException("syntax error. Expected token='(' :" + token.getToken());
        }
    }

    public void checkEqualToken() {
        final Token token = this.nextToken();
        if (token == null) {
            throw new RuntimeException("parse error. token is null. Expected token='='");
        }
        if (token != OracleNetConnectionDescriptorTokenizer.TOKEN_EQUAL_OBJECT) {
            throw new RuntimeException("Syntax error. Expected token='=' :" + token.getToken());
        }
    }

    public void checkEndToken() {
        final Token token = this.nextToken();
        if (token == null) {
            throw new RuntimeException("parse error. token is null. Expected token=')");
        }
        if (token != OracleNetConnectionDescriptorTokenizer.TOKEN_KEY_END_OBJECT) {
            throw new RuntimeException("Syntax error. Expected token=')' :" + token.getToken());
        }
    }

    public Token getLiteralToken() {
        final Token token = this.nextToken();
        if (token == null) {
            throw new RuntimeException("parse error. token is null. Expected token='LITERAL'");
        }
        if (token.getType() != 3) {
            throw new RuntimeException("Syntax error. Expected token='LITERAL'' :" + token.getToken());
        }
        return token;
    }

    public Token getLiteralToken(final String expectedValue) {
        final Token token = this.nextToken();
        if (token == null) {
            throw new RuntimeException("parse error. token is null. Expected token='LITERAL'");
        }
        if (token.getType() != 3) {
            throw new RuntimeException("Syntax error. Expected token='LITERAL' :" + token.getToken());
        }
        if (!expectedValue.equals(token.getToken())) {
            throw new RuntimeException("Syntax error. Expected token=" + expectedValue + "' :" + token.getToken());
        }
        return token;
    }

    static {
        TOKEN_KEY_START_OBJECT = new Token(String.valueOf('('), 0);
        TOKEN_KEY_END_OBJECT = new Token(String.valueOf(')'), 1);
        TOKEN_EQUAL_OBJECT = new Token(String.valueOf('='), 2);
        TOKEN_EOF_OBJECT = new Token("EOF", -1);
    }
}
