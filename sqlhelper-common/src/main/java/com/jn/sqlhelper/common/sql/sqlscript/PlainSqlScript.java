/**
 * Copyright 2010-2015 Axel Fontaine
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.sqlhelper.common.sql.sqlscript;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sql script containing a series of statements terminated by a delimiter (eg: ;).
 * Single-line (--) and multi-line (/* * /) comments are stripped and ignored.
 */
public class PlainSqlScript{
    private static final Logger logger = LoggerFactory.getLogger(PlainSqlScript.class);
    private Resource resource;
    @NonNull
    private String encoding = Charsets.UTF_8.name();
    @Nullable
    private String dialect;

    public PlainSqlScript(Resource resource, String encoding) {
        this(null, resource, encoding);
    }
    public PlainSqlScript(String dialect, Resource resource, String encoding) {
        this.dialect = dialect;
        this.resource = resource;
        this.encoding = encoding;
    }

    public Resource getResource() {
        return resource;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
