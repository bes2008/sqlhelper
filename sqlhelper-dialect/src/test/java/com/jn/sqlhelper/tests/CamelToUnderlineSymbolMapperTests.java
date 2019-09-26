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

import com.jn.sqlhelper.common.symbolmapper.SqlSymbolMapper;
import com.jn.sqlhelper.common.symbolmapper.CamelToUnderlineSymbolMapper;
import org.junit.Assert;
import org.junit.Test;

public class CamelToUnderlineSymbolMapperTests {
    @Test
    public void testUpperCase() {
        SqlSymbolMapper mapper = new CamelToUnderlineSymbolMapper(true);
        Assert.assertEquals("USERID", mapper.apply("userid"));
        Assert.assertEquals("USER_ID", mapper.apply("userId"));
        Assert.assertEquals("USER_IDSTRING", mapper.apply("userIdstring"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("userId_string"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("userId__string"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("user_Id__string"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("user_Id__string__"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("__user_Id__string"));
        Assert.assertEquals("USER_ID_STRING", mapper.apply("__user_Id__string__"));
        Assert.assertEquals("A_B_CUSERID", mapper.apply("ABCuserid"));
    }

    @Test
    public void testLowerCase() {
        SqlSymbolMapper mapper = new CamelToUnderlineSymbolMapper(false);
        Assert.assertEquals("userid", mapper.apply("userid"));
        Assert.assertEquals("user_id", mapper.apply("userId"));
        Assert.assertEquals("user_idstring", mapper.apply("userIdstring"));
        Assert.assertEquals("user_id_string", mapper.apply("userId_string"));
        Assert.assertEquals("user_id_string", mapper.apply("userId__string"));
        Assert.assertEquals("user_id_string", mapper.apply("user_Id__string"));
        Assert.assertEquals("user_id_string", mapper.apply("user_Id__string__"));
        Assert.assertEquals("user_id_string", mapper.apply("__user_Id__string"));
        Assert.assertEquals("user_id_string", mapper.apply("__user_Id__string__"));
        Assert.assertEquals("a_b_cuserid", mapper.apply("ABCuserid"));
    }

    @Test
    public void testsSuffix() {
        SqlSymbolMapper mapper = new CamelToUnderlineSymbolMapper(false, "_TABLE");
        Assert.assertEquals("userid_table", mapper.apply("userid"));
        Assert.assertEquals("user_id_table", mapper.apply("userId"));
        Assert.assertEquals("user_idstring_table", mapper.apply("userIdstring"));
        Assert.assertEquals("user_id_string_table", mapper.apply("userId_string"));
        Assert.assertEquals("user_id_string_table", mapper.apply("userId__string"));
        Assert.assertEquals("user_id_string_table", mapper.apply("user_Id__string"));
        Assert.assertEquals("user_id_string_table", mapper.apply("user_Id__string__"));
        Assert.assertEquals("user_id_string_table", mapper.apply("__user_Id__string"));
        Assert.assertEquals("user_id_string_table", mapper.apply("__user_Id__string__"));
        Assert.assertEquals("a_b_cuserid_table", mapper.apply("ABCuserid"));
    }

    @Test
    public void testsSuffix1() {
        SqlSymbolMapper mapper = new CamelToUnderlineSymbolMapper(false, "_");
        Assert.assertEquals("userid_", mapper.apply("userid"));
        Assert.assertEquals("user_id_", mapper.apply("userId"));
        Assert.assertEquals("user_idstring_", mapper.apply("userIdstring"));
        Assert.assertEquals("user_id_string_", mapper.apply("userId_string"));
        Assert.assertEquals("user_id_string_", mapper.apply("userId__string"));
        Assert.assertEquals("user_id_string_", mapper.apply("user_Id__string"));
        Assert.assertEquals("user_id_string_", mapper.apply("user_Id__string__"));
        Assert.assertEquals("user_id_string_", mapper.apply("__user_Id__string"));
        Assert.assertEquals("user_id_string_", mapper.apply("__user_Id__string__"));
        Assert.assertEquals("a_b_cuserid_", mapper.apply("ABCuserid"));
    }

    @Test
    public void testsPrefix() {
        SqlSymbolMapper mapper = new CamelToUnderlineSymbolMapper("T_", false);
        Assert.assertEquals("t_userid", mapper.apply("userid"));
        Assert.assertEquals("t_user_id", mapper.apply("userId"));
        Assert.assertEquals("t_user_idstring", mapper.apply("userIdstring"));
        Assert.assertEquals("t_user_id_string", mapper.apply("userId_string"));
        Assert.assertEquals("t_user_id_string", mapper.apply("userId__string"));
        Assert.assertEquals("t_user_id_string", mapper.apply("user_Id__string"));
        Assert.assertEquals("t_user_id_string", mapper.apply("user_Id__string__"));
        Assert.assertEquals("t_user_id_string", mapper.apply("__user_Id__string"));
        Assert.assertEquals("t_user_id_string", mapper.apply("__user_Id__string__"));
        Assert.assertEquals("t_a_b_cuserid", mapper.apply("ABCuserid"));
    }
}
