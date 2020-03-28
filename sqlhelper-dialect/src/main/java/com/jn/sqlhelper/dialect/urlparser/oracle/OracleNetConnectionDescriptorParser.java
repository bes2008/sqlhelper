
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

import com.jn.langx.exception.IllegalValueException;

public class OracleNetConnectionDescriptorParser {
    private static String THIN;
    private static String OCI;
    private String url;
    private String normalizedUrl;
    private DriverType driverType;
    private OracleNetConnectionDescriptorTokenizer tokenizer;

    public OracleNetConnectionDescriptorParser(final String url) {
        this.url = url;
        this.normalizedUrl = url.toLowerCase();
        this.tokenizer = new OracleNetConnectionDescriptorTokenizer(this.normalizedUrl);
    }

    public KeyValue parse() {
        int position;
        if (this.normalizedUrl.startsWith(OracleNetConnectionDescriptorParser.THIN)) {
            position = this.nextPosition(OracleNetConnectionDescriptorParser.THIN);
            this.driverType = DriverType.THIN;
        } else {
            if (!this.normalizedUrl.startsWith(OracleNetConnectionDescriptorParser.OCI)) {
                throw new IllegalArgumentException("invalid oracle jdbc url. expected token:(" + OracleNetConnectionDescriptorParser.THIN + " or " + OracleNetConnectionDescriptorParser.OCI + ") url:" + this.url);
            }
            position = this.nextPosition(OracleNetConnectionDescriptorParser.OCI);
            this.driverType = DriverType.OCI;
        }
        this.tokenizer.setPosition(position);
        this.tokenizer.parse();
        final KeyValue keyValue = this.parseKeyValue();
        this.checkEof();
        return keyValue;
    }

    private void checkEof() {
        final Token eof = this.tokenizer.nextToken();
        if (eof == null) {
            throw new IllegalValueException("parsing error. expected token:'EOF' token:null");
        }
        if (eof != OracleNetConnectionDescriptorTokenizer.TOKEN_EOF_OBJECT) {
            throw new IllegalValueException("parsing error. expected token:'EOF' token:" + eof);
        }
    }

    public DriverType getDriverType() {
        return this.driverType;
    }

    private int nextPosition(final String driverUrl) {
        final int thinLength = driverUrl.length();
        if (this.normalizedUrl.startsWith(":@", thinLength)) {
            return thinLength + 2;
        }
        if (this.normalizedUrl.startsWith("@", thinLength)) {
            return thinLength + 1;
        }
        throw new RuntimeException("invalid oracle jdbc url:" + driverUrl);
    }

    private KeyValue parseKeyValue() {
        this.tokenizer.checkStartToken();
        final KeyValue keyValue = new KeyValue();
        final Token literalToken = this.tokenizer.getLiteralToken();
        keyValue.setKey(literalToken.getToken());
        this.tokenizer.checkEqualToken();
        boolean nonTerminalValue = false;
        while (true) {
            final Token token = this.tokenizer.lookAheadToken();
            if (token == null) {
                throw new RuntimeException("Syntax error. lookAheadToken is null");
            }
            if (token.getType() == 0) {
                nonTerminalValue = true;
                final KeyValue child = this.parseKeyValue();
                keyValue.addKeyValueList(child);
                final Token endCheck = this.tokenizer.lookAheadToken();
                if (endCheck == OracleNetConnectionDescriptorTokenizer.TOKEN_KEY_END_OBJECT) {
                    this.tokenizer.nextPosition();
                    return keyValue;
                }
                continue;
            } else if (token.getType() == 3) {
                if (nonTerminalValue) {
                    throw new RuntimeException("Syntax error. expected token:'(' or ')' :" + token.getToken());
                }
                this.tokenizer.nextPosition();
                keyValue.setValue(token.getToken());
                this.tokenizer.checkEndToken();
                return keyValue;
            } else {
                if (token.getType() == 1) {
                    this.tokenizer.nextPosition();
                    return keyValue;
                }
                throw new RuntimeException("Syntax error. " + token.getToken());
            }
        }
    }

    static {
        OracleNetConnectionDescriptorParser.THIN = "jdbc:oracle:thin";
        OracleNetConnectionDescriptorParser.OCI = "jdbc:oracle:oci";
    }
}
