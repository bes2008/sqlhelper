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

package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.ThrowableFunction;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class ArrayRowMapper implements RowMapper<Object[]> {
    private static final Logger logger = LoggerFactory.getLogger(ArrayRowMapper.class);

    @Override
    public Object[] mapping(final ResultSet row, int currentRowIndex, ResultSetDescription resultSetDescription) {
        final Object[] result = new Object[resultSetDescription.getColumnCount()];
        Collects.forEach(Arrs.range(resultSetDescription.getColumnCount()), new Consumer<Integer>() {
            @Override
            public void accept(final Integer index) {
                result[index] = Throwables.ignoreThrowable(logger, null, new ThrowableFunction<Object, Object>() {
                    @Override
                    public Object doFun(Object object) throws Throwable {
                        return ResultSets.getResultSetValue(row, index + 1);
                    }
                }, null);
            }
        });
        return result;
    }
}
