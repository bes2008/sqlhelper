/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.session.factory;

import com.jn.langx.factory.Provider;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 默认的动态数据源自动获取是基于 MyBatis Mapper 做的，这个能解决大部分场景，但是不能解决全部的场景。
 * 在使用MyBatis时，很多情况下，是可以直接使用SqlSessionFactory的，例如 {@linkplain com.jn.sqlhelper.mybatis.batch.MybatisBatchUpdaters}
 * <p>
 * 当使用 动态数据源时，往Spring容器里注入的 SqlSessionFactory其实是 DynamicSqlSessionFactory，如何获取到真是的 SQLSessionFactory呢？
 * <p>
 * 这个设计就是为了解决这个问题的。
 *
 * @param <I>
 */

public interface SqlSessionFactoryProvider<I> extends Provider<I, SqlSessionFactory> {

}
