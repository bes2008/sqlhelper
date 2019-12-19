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

package com.jn.sqlhelper.mybatis;

import com.jn.langx.annotation.NonNull;
import com.jn.sqlhelper.mybatis.batch.MybatisBatchStatement;
import com.jn.sqlhelper.mybatis.plugins.pagination.CustomVendorDatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;

public class MybatisUtils {
    private static VendorDatabaseIdProvider vendorDatabaseIdProvider;

    static {
        vendorDatabaseIdProvider = new CustomVendorDatabaseIdProvider();
    }

    public static VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        return vendorDatabaseIdProvider;
    }

    public static boolean isPagingRowBounds(RowBounds rowBounds) {
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return false;
        }
        return rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET || rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT;
    }

    public static boolean hasStatement(@NonNull SqlSessionFactory sessionFactory, String statementName){
        return sessionFactory.getConfiguration().hasStatement(statementName);
    }
}
