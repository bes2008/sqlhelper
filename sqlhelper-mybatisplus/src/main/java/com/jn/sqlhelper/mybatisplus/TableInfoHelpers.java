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

package com.jn.sqlhelper.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TableInfoHelpers {
    private static final Logger logger = LoggerFactory.getLogger(TableInfoHelpers.class);

    private static final Class tableInfoHelperClass;

    static {
        Class helperClass = null;
        try {
            // 3.2.0+
            if (ClassLoaders.hasClass("com.baomidou.mybatisplus.core.metadata.TableInfoHelper", TableInfoHelpers.class.getClassLoader())) {
                helperClass = ClassLoaders.loadClass("com.baomidou.mybatisplus.core.metadata.TableInfoHelper", TableInfoHelpers.class.getClassLoader());
            }
            // 3.1.0
            if (helperClass == null && ClassLoaders.hasClass("com.baomidou.mybatisplus.core.toolkit.TableInfoHelper", TableInfoHelpers.class.getClassLoader())) {
                helperClass = ClassLoaders.loadClass("com.baomidou.mybatisplus.core.toolkit.TableInfoHelper", TableInfoHelpers.class.getClassLoader());
            }
        }catch (Throwable ex){
            logger.warn("Can't find the com.baomidou.mybatisplus.core.metadata.TableInfoHelper or com.baomidou.mybatisplus.core.toolkit.TableInfoHelper");
        }
        tableInfoHelperClass = helperClass;
    }

    public static TableInfo getTableInfo(Class<?> clazz){
        try {
            return Reflects.<TableInfo>invokeAnyStaticMethod(tableInfoHelperClass,"getTableInfo", new Class[]{Class.class}, new Object[]{clazz}, true, true);
        }catch (Throwable ex){
            logger.warn(ex.getMessage(), ex);
            return null;
        }
    }

    public static List<TableInfo> getTableInfos(){
        try {
            return Reflects.<List<TableInfo>>invokeAnyStaticMethod(tableInfoHelperClass,"getTableInfos", null, null, true, true);
        }catch (Throwable ex){
            logger.warn(ex.getMessage(), ex);
            return null;
        }
    }
}
