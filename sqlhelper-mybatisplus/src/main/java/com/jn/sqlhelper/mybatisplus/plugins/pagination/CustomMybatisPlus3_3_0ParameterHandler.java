package com.jn.sqlhelper.mybatisplus.plugins.pagination;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.sqlhelper.mybatis.plugins.CustomMybatisParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 从mybatis-plus 3.3.0 版本开始使用这个Handler
 * <p>
 * code migrate from mybatis-plus 3.4.2 version
 */
public class CustomMybatisPlus3_3_0ParameterHandler extends CustomMybatisParameterHandler {


    public CustomMybatisPlus3_3_0ParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, parameterObject, boundSql);
    }

    @Override
    protected Object processParameter(Object parameter) {
        /* 只处理插入或更新操作 */
        if (parameter != null
                && (SqlCommandType.INSERT == this.sqlCommandType || SqlCommandType.UPDATE == this.sqlCommandType)) {
            //检查 parameterObject
            if (Primitives.isPrimitiveOrPrimitiveWrapperType(parameter.getClass()) || parameter.getClass() == String.class) {
                return parameter;
            }
            Collection<Object> parameters = getParameters(parameter);
            if (null != parameters) {
                // 感觉这里可以稍微优化一下，理论上都是同一个.
                // 原始代码：parameters.forEach(this::process);
                Collects.forEach(parameters, new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        process(o);
                    }
                });
            } else {
                process(parameter);
            }
        }
        return parameter;
    }

    private void process(Object parameter) {
        if (parameter != null) {
            TableInfo tableInfo = null;
            Object entity = parameter;
            if (parameter instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) parameter;
                if (map.containsKey(Constants.ENTITY)) {
                    Object et = map.get(Constants.ENTITY);
                    if (et != null) {
                        entity = et;
                        tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
                    }
                }
            } else {
                tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
            }
            if (tableInfo != null) {
                //到这里就应该转换到实体参数对象了,因为填充和ID处理都是争对实体对象处理的,不用传递原参数对象下去.
                MetaObject metaObject = this.configuration.newMetaObject(entity);
                if (SqlCommandType.INSERT == this.sqlCommandType) {
                    populateKeys(tableInfo, metaObject, entity);
                    insertFill(metaObject, tableInfo);
                } else {
                    updateFill(metaObject, tableInfo);
                }
            }
        }
    }


    protected void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
        final IdType idType = tableInfo.getIdType();
        final String keyProperty = tableInfo.getKeyProperty();
        if (Strings.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
            final IdentifierGenerator identifierGenerator = GlobalConfigUtils.getGlobalConfig(this.configuration).getIdentifierGenerator();
            Object idValue = metaObject.getValue(keyProperty);
            if (Objs.isNotEmpty(idValue)) {
                if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
                    if (Number.class.isAssignableFrom(tableInfo.getKeyType())) {
                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity));
                    } else {
                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity).toString());
                    }
                } else if (idType.getKey() == IdType.ASSIGN_UUID.getKey()) {
                    metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
                }
            }
        }
    }


    protected void insertFill(MetaObject metaObject, TableInfo tableInfo) {
        /*
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openInsertFill() && tableInfo.isWithInsertFill()) {
                metaObjectHandler.insertFill(metaObject);
            }
        });
        */
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(this.configuration);
        MetaObjectHandler metaObjectHandler = globalConfig.getMetaObjectHandler();
        if (metaObjectHandler != null) {
            if (metaObjectHandler.openInsertFill() && tableInfo.isWithInsertFill()) {
                metaObjectHandler.insertFill(metaObject);
            }
        }
    }

    protected void updateFill(MetaObject metaObject, TableInfo tableInfo) {
        /*
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openUpdateFill() && tableInfo.isWithUpdateFill()) {
                metaObjectHandler.updateFill(metaObject);
            }
        });
         */

        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(this.configuration);
        MetaObjectHandler metaObjectHandler = globalConfig.getMetaObjectHandler();
        if (metaObjectHandler != null) {
            if (metaObjectHandler.openUpdateFill() && tableInfo.isWithUpdateFill()) {
                metaObjectHandler.updateFill(metaObject);
            }
        }
    }

    /**
     * 处理正常批量插入逻辑
     * <p>
     * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
     * wrapCollection 实现 StrictMap 封装逻辑
     * </p>
     *
     * @return 集合参数
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Collection<Object> getParameters(Object parameterObject) {
        Collection<Object> parameters = null;
        if (parameterObject instanceof Collection) {
            parameters = (Collection) parameterObject;
        } else if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
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

}
