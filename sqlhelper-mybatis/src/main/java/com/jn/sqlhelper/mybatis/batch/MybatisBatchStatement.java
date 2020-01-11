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

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchStatement;

public class MybatisBatchStatement implements BatchStatement {
    private BatchMode batchMode = BatchMode.JDBC_BATCH;
    private String statementId; // optional
    private Class mapperClass; // optional
    private String statementIdFQN; // the sql, required

    public MybatisBatchStatement() {

    }

    public MybatisBatchStatement(@NonNull String statementIdFQN) {
        this(BatchMode.JDBC_BATCH, statementIdFQN);
    }

    public MybatisBatchStatement(@Nullable BatchMode batchType, @NonNull String statementIdFQN) {
        setBatchMode(batchType);
        setStatementIdFQN(statementIdFQN);
    }

    public MybatisBatchStatement(@NonNull Class mapperClass, @NonNull String statementId) {
        this(BatchMode.JDBC_BATCH, mapperClass, statementId);
    }

    public MybatisBatchStatement(@Nullable BatchMode batchType, @NonNull Class mapperClass, @NonNull String statementId) {
        setBatchMode(batchType);
        setMapperClass(mapperClass);
        setStatementId(statementId);
        setStatementIdFQN(Reflects.getFQNClassName(mapperClass) + "." + statementId);
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
        Preconditions.checkNotNull(statementId, "mybatis sql statement id is null");
        this.statementId = statementId;
    }

    public Class getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class mapperClass) {
        Preconditions.checkNotNull(mapperClass, "mybatis mapper class is null");
        this.mapperClass = mapperClass;
    }

    @Override
    public String getSql() {
        return statementIdFQN;
    }

    public String getStatementIdFQN() {
        return statementIdFQN;
    }

    public void setStatementIdFQN(String statementIdFQN) {
        this.statementIdFQN = Preconditions.checkNotNull(statementIdFQN);
    }
}
