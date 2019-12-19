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

import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchType;

public class MybatisBatchStatement implements BatchStatement<String> {
    private BatchType batchType = BatchType.JDBC_BATCH;
    private String statementId;
    private Class mapperClass;

    public MybatisBatchStatement() {

    }

    public MybatisBatchStatement(Class mapperClass, String statementId) {
        this(BatchType.JDBC_BATCH, mapperClass, statementId);
    }

    public MybatisBatchStatement(BatchType batchType, Class mapperClass, String statementId) {
        setBatchType(batchType);
        setMapperClass(mapperClass);
        setSql(statementId);
    }

    @Override
    public BatchType getBatchType() {
        return batchType;
    }

    @Override
    public void setBatchType(BatchType batchType) {
        this.batchType = batchType;
    }

    @Override
    public String getSql() {
        return statementId;
    }

    public void setSql(String statementId) {
        this.statementId = statementId;
    }

    public Class getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class mapperClass) {
        this.mapperClass = mapperClass;
    }
}
