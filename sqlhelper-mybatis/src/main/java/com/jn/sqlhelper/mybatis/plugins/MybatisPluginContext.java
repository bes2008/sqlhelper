/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.mybatis.plugins;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;

public class MybatisPluginContext implements Initializable {
    private static final MybatisPluginContext instance = new MybatisPluginContext();
    private boolean inited = false;

    private MybatisPluginContext() {
    }

    public static MybatisPluginContext getInstance() {
        return instance;
    }

    private SQLStatementInstrumentor instrumentor;

    public SQLStatementInstrumentor getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLStatementInstrumentor instrumentor) {
        this.instrumentor = instrumentor;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            Preconditions.checkNotNull(instrumentor, "SQL Instrumentor is null");
            instrumentor.init();
            inited = true;
        }
    }

    public void setInstrumentorConfig(SQLInstrumentConfig config) {
        instrumentor.setConfig(config);
    }
}
