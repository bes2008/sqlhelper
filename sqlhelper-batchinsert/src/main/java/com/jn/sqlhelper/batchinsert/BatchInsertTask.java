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

package com.jn.sqlhelper.batchinsert;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.Callable;

public abstract class BatchInsertTask implements Callable<BatchInsertResult> {
    protected Random random;
    protected String start;
    protected ConnectionFactory connFactory;

    public BatchInsertTask(String startTime, long seed) {
        this.start = startTime;
        this.random = new Random(seed);
    }

    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }

    @Override
    public BatchInsertResult call() throws SQLException {
        int realInsertRows = batchInsertTable();
        return new BatchInsertResult(start, getExpectInsertRows(), realInsertRows);
    }

    protected abstract int getExpectInsertRows();

    protected abstract int batchInsertTable() throws SQLException;
}
