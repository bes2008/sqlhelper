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

package com.jn.sqlhelper.mybatisplus.plugins.pagination;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public class CustomMybatisPlusScriptLanguageDriver extends XMLLanguageDriver {
    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        String mybatisPlusVersion = MybatisPlusVersions.getMyBatisPlusVersion();
        // 3.0.0 <= mybatis-plus version < 3.3.0
        if (MybatisPlusVersions.UNKNOWN.equals(mybatisPlusVersion) || "3.3.0".compareTo(mybatisPlusVersion) > 0) {
            return new CustomMybatisPlus3_0_0ParameterHandler(mappedStatement, parameterObject, boundSql);
        } else {
            // mybatis-plus version >= 3.3.0
            return new CustomMybatisPlus3_3_0ParameterHandler(mappedStatement, parameterObject, boundSql);
        }
    }
}
