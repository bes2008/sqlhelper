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

package com.jn.sqlhelper.mybatis.batch;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchMode;

public class MybatisBatchStatement implements BatchStatement {
    private BatchMode batchMode = BatchMode.JDBC_BATCH;
    private String statementId;
    private Class mapperClass;

    public MybatisBatchStatement() {

    }

    public MybatisBatchStatement(Class mapperClass, String statementId) {
        this(BatchMode.JDBC_BATCH, mapperClass, statementId);
    }

    public MybatisBatchStatement(BatchMode batchType, Class mapperClass, String statementId) {
        setBatchMode(batchType);
        setMapperClass(mapperClass);
        setStatementId(statementId);
    }

    @Override
    public BatchMode getBatchMode() {
        return batchMode;
    }

    @Override
    public void setBatchMode(BatchMode batchMode) {
        this.batchMode = batchMode;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public Class getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    public String getSql() {
        Preconditions.checkNotNull(Emptys.isNotEmpty(statementId), "Sql statement id is null");
        Preconditions.checkNotNull(mapperClass, "The mapper class is null");
        return Reflects.getFQNClassName(mapperClass) + "." + statementId;
    }

}
