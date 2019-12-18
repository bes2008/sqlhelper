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

import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchType;
import com.jn.sqlhelper.mybatis.mapper.BaseMapper;
import com.jn.sqlhelper.mybatis.mapper.Entity;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class SimpleBatchUpdater<E extends Entity<ID>, ID> extends MybatisBatchUpdater<E> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleBatchUpdater.class);

    @Override
    public BatchResult<E> batchUpdate(BatchStatement statement, List<E> entities) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchType() == BatchType.SIMPLE);

        Preconditions.checkNotNull(sessionFactory);
        Preconditions.checkNotNull(mapperClass);
        SqlSession session = sessionFactory.openSession(true);
        final BaseMapper mapper = (BaseMapper) session.getMapper(mapperClass);
        int updated = 0;
        String method = statement.getSql();
        for (int i = 0; i < entities.size(); i++) {
            E entity = entities.get(i);
            if (Objects.isNull(method) || (!INSERT.equals(method) && !UPDATE.equals(method))) {
                E o = (E) mapper.selectById(entity.getId());
                if (o != null) {
                    mapper.update(entity);
                } else {
                    mapper.insert(entity);
                }
                updated++;
            } else {
                if (INSERT.equals(method)) {
                    mapper.insert(entity);
                } else {
                    mapper.update(entity);
                }
            }
        }
        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(entities);
        result.setStatement(statement);
        result.setRowsAffected(updated);
        return result;
    }
}
