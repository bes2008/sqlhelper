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

package com.jn.sqlhelper.common.batch;

import com.jn.langx.util.collection.Collects;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BatchResult<E> {
    private BatchStatement statement;
    private List<E> parameters;
    private volatile int rowsAffected;
    private final Set<Throwable> throwables = new CopyOnWriteArraySet<Throwable>();

    public BatchStatement getStatement() {
        return statement;
    }

    public void setStatement(BatchStatement statement) {
        this.statement = statement;
    }

    public List<E> getParameters() {
        return parameters;
    }

    public void setParameters(List<E> parameters) {
        this.parameters = parameters;
    }

    public int getRowsAffected() {
        return rowsAffected;
    }

    public void setRowsAffected(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    public List<Throwable> getThrowables() {
        return Collects.asList(throwables);
    }

    public void setThrowables(List<Throwable> throwables) {
        this.throwables.addAll(throwables);
    }

    public void addThrowable(Throwable ex) {
        this.throwables.add(ex);
    }

    public boolean hasThrowable() {
        return !this.throwables.isEmpty();
    }

    public String getSql() {
        return statement.getSql();
    }
}
