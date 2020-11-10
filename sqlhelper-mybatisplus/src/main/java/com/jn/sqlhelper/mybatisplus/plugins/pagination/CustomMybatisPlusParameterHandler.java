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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.sqlhelper.mybatis.plugins.CustomMybatisParameterHandler;
import com.jn.sqlhelper.mybatisplus.TableInfoHelpers;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import java.util.*;

public class CustomMybatisPlusParameterHandler extends CustomMybatisParameterHandler {
    protected Object parameterObject;
    public CustomMybatisPlusParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, processBatch(mappedStatement, parameterObject), boundSql);
        this.parameterObject = parameterObject;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    public static Object processBatch(MappedStatement ms, Object parameterObject) {
        if (null != parameterObject && !Primitives.isPrimitiveOrPrimitiveWrapperType(parameterObject.getClass()) && parameterObject.getClass() != String.class) {
            GlobalConfig globalConfig =GlobalConfigUtils.getGlobalConfig(ms.getConfiguration());
            MetaObjectHandler metaObjectHandler = globalConfig.getMetaObjectHandler();
            boolean isFill = false;
            boolean isInsert = false;
            if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
                isFill = true;
                isInsert = true;
            } else if (ms.getSqlCommandType() == SqlCommandType.UPDATE && metaObjectHandler != null && metaObjectHandler.openUpdateFill()) {
                isFill = true;
            }

            if (isFill) {
                Collection<Object> parameters = getParameters(parameterObject);
                Object et;
                if (null != parameters) {
                    List<Object> objList = new ArrayList();
                    Iterator var10 = parameters.iterator();

                    while (var10.hasNext()) {
                        et = var10.next();
                        TableInfo tableInfo = TableInfoHelpers.getTableInfo(et.getClass());
                        if (null != tableInfo) {
                            objList.add(populateKeys(metaObjectHandler, tableInfo, ms, et, isInsert));
                        } else {
                            objList.add(et);
                        }
                    }

                    return objList;
                } else {
                    TableInfo tableInfo = null;
                    if (parameterObject instanceof Map) {
                        Map<?, ?> map = (Map) parameterObject;
                        if (map.containsKey("et")) {
                            et = map.get("et");
                            if (et != null) {
                                if (et instanceof Map) {
                                    Map<?, ?> realEtMap = (Map) et;
                                    if (realEtMap.containsKey("MP_OPTLOCK_ET_ORIGINAL")) {
                                        tableInfo = TableInfoHelpers.getTableInfo(realEtMap.get("MP_OPTLOCK_ET_ORIGINAL").getClass());
                                    }
                                } else {
                                    tableInfo = TableInfoHelpers.getTableInfo(et.getClass());
                                }
                            }
                        }
                    } else {
                        tableInfo = TableInfoHelpers.getTableInfo(parameterObject.getClass());
                    }

                    return populateKeys(metaObjectHandler, tableInfo, ms, parameterObject, isInsert);
                }
            } else {
                return parameterObject;
            }
        } else {
            return null;
        }
    }

    public static Collection<Object> getParameters(Object parameter) {
        Collection<Object> parameters = null;
        if (parameter instanceof Collection) {
            parameters = (Collection) parameter;
        } else if (parameter instanceof Map) {
            Map parameterMap = (Map) parameter;
            if (parameterMap.containsKey("collection")) {
                parameters = (Collection) parameterMap.get("collection");
            } else if (parameterMap.containsKey("list")) {
                parameters = (List) parameterMap.get("list");
            } else if (parameterMap.containsKey("array")) {
                parameters = Arrays.asList((Object[]) ((Object[]) parameterMap.get("array")));
            }
        }

        return (Collection) parameters;
    }

    public static Object populateKeys(MetaObjectHandler metaObjectHandler, TableInfo tableInfo, MappedStatement ms, Object parameterObject, boolean isInsert) {
        if (null == tableInfo) {
            return parameterObject;
        } else {
            MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
            if (isInsert && !Strings.isEmpty(tableInfo.getKeyProperty()) && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 3) {
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
                if (StringUtils.checkValNull(idValue)) {
                    if (tableInfo.getIdType() == IdType.ID_WORKER) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
                    } else if (tableInfo.getIdType() == IdType.ID_WORKER_STR) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getIdStr());
                    } else if (tableInfo.getIdType() == IdType.UUID) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.get32UUID());
                    }
                }
            }

            if (metaObjectHandler != null) {
                if (isInsert && metaObjectHandler.openInsertFill()) {
                    metaObjectHandler.insertFill(metaObject);
                } else if (!isInsert) {
                    metaObjectHandler.updateFill(metaObject);
                }
            }

            return metaObject.getOriginalObject();
        }
    }

}
