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
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.sqlhelper.mybatis.plugins.CustomMybatisParameterHandler;
import com.jn.sqlhelper.mybatisplus.tableinfo.TableInfoHelpers;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import java.util.*;

/**
 * 最多用到mybatis-plus 3.2.x 版本
 */
public class CustomMybatisPlusParameterHandler extends CustomMybatisParameterHandler {
    protected Object originalParameterObject;

    public CustomMybatisPlusParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
        this.originalParameterObject = parameterObject;
    }

    @Override
    protected Object processParameter(Object parameterObject) {
        return processBatch(this.mappedStatement,parameterObject);
    }

    @Override
    protected Object getOriginParameterObject() {
        return this.originalParameterObject;
    }

    /*
     * 此逻辑自定义全局参数填充器 mybatis-plus的逻辑自定义完以后会修改父类的parameterObject.故必须重写setParameters中获取parameterObject方法
     * e.g.
     *  如果每个表都有一些公共字段创建.就可以使用此逻辑
     *  实现MetaObjectHandler 接口即可
     */
    public static Object processBatch(MappedStatement ms, Object parameterObject) {
        if (null != parameterObject && !Primitives.isPrimitiveOrPrimitiveWrapperType(parameterObject.getClass()) && parameterObject.getClass() != String.class) {
            GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(ms.getConfiguration());
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
                    Iterator iter = parameters.iterator();

                    while (iter.hasNext()) {
                        et = iter.next();
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
                parameters = Arrays.asList((Object[]) parameterMap.get("array"));
            }
        }

        return parameters;
    }

    public static Object populateKeys(MetaObjectHandler metaObjectHandler, TableInfo tableInfo, MappedStatement ms, Object parameterObject, boolean isInsert) {
        if (null == tableInfo) {
            return parameterObject;
        } else {
            MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
            String keyProperty = tableInfo.getKeyProperty();
            if (isInsert && !Strings.isEmpty(keyProperty) && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 3) {
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
                if (Objs.isEmpty(idValue)) {
                    if (tableInfo.getIdType().getKey() == IdType.ID_WORKER.getKey()) {
                        // 最新版本里，这里加了个 ASSIGN_ID:3， 并把原来的 进行了调整：ID_WORKER:3 ID_WORKER_STR:3
                        if (Reflects.isSubClassOrEquals(Number.class, tableInfo.getKeyType())) {
                            metaObject.setValue(keyProperty, IdWorker.getId());
                        } else {
                            metaObject.setValue(keyProperty, IdWorker.getIdStr());
                        }
                    } else if (tableInfo.getIdType().getKey() == IdType.ID_WORKER_STR.getKey()) {
                        metaObject.setValue(keyProperty, IdWorker.getIdStr());
                    } else if (tableInfo.getIdType().getKey() == IdType.UUID.getKey()) {
                        metaObject.setValue(keyProperty, IdWorker.get32UUID());
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

    @Override
    protected boolean useOriginalParameter(boolean isPagingRequest) {
        return true;
    }
}
