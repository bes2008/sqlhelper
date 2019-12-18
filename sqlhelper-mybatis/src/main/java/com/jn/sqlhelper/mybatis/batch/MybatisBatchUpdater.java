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

import com.jn.sqlhelper.common.batch.BatchUpdater;
import org.apache.ibatis.session.SqlSessionFactory;

public abstract class MybatisBatchUpdater<E> implements BatchUpdater<E> {

    public static final String INSERT = "insert";
    public static final String UPDATE = "update";

    protected SqlSessionFactory sessionFactory;
    protected Class<E> mapperClass;

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Class<E> getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class<E> mapperClass) {
        this.mapperClass = mapperClass;
    }
}
