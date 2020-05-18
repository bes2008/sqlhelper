/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.dialect.instrument;

/**
 * 条件注入的位置，可以用在 where 注入，也可以用在 group by 注入
 */
public enum InjectPosition {
    /**
     * 注入在所有的表达式之前
     */
    FIRST,
    /**
     * 注入在所有的表达式之后
     */
    LAST,
    /**
     * 自动选择最佳注入位置，其实就是根据表的索引去选择
     */
    BEST;
}
