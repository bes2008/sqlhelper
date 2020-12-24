/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource.key.router;

import com.jn.langx.Named;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.function.Function2;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.util.List;

public interface DataSourceKeyRouter extends Function2<List<DataSourceKey>, MethodInvocation, DataSourceKey>, Named {
    /**
     * @param dataSourceKeys 该参数为经过筛选后的 key，筛选过程可以参见 {@link com.jn.sqlhelper.datasource.key.DataSourceKeySelector#select(AbstractDataSourceKeyRouter, MethodInvocation)}
     *                       如果开启了故障转移，则在筛选时，会自动的移除掉故障的。
     * @return 返回选中的datasource key, 如果没有合适的，返回 null 即可.
     */
    @Override
    DataSourceKey apply(@NonNull List<DataSourceKey> dataSourceKeys, @Nullable MethodInvocation methodInvocation);

}
