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

package com.jn.sqlhelper.tests;

import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import com.jn.sqlhelper.dialect.symbolmapper.CamelToUnderlineSymbolMapper;
import org.junit.Test;

public class SqlStyleOrderByBuilderTests {
    @Test
    public void test() {
        SqlStyleOrderByBuilder builder = new SqlStyleOrderByBuilder();
        builder.setSqlSymbolMapper(new CamelToUnderlineSymbolMapper("C_", true));

        System.out.println(builder.build(null).toString());
        System.out.println(builder.build(" ").toString());
        System.out.println(builder.build("  \t  ").toString());
        System.out.println(builder.build(" a ").toString());
        System.out.println(builder.build(" a ,").toString());
        System.out.println(builder.build(" a,").toString());
        System.out.println(builder.build(",ad").toString());
        System.out.println(builder.build(" a   \t asc").toString());
        System.out.println(builder.build(" abc   \n  desc").toString());
        System.out.println(builder.build(" a , b ").toString());
        System.out.println(builder.build(" a, b ").toString());
    }
}
