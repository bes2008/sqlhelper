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

import com.jn.sqlhelper.dialect.DialectRegistry;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.RowBounds;

public class MybatisUtils {
    private static final VendorDatabaseIdProvider vendorDatabaseIdProvider = new VendorDatabaseIdProvider();

    static {
        vendorDatabaseIdProvider.setProperties(DialectRegistry.getVendorDatabaseIdMappings());
    }

    public static VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        return vendorDatabaseIdProvider;
    }

    public static boolean isValidRowBounds(RowBounds rowBounds){
        return rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET || rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT;
    }
}
