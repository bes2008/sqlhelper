package com.jn.sqlhelper.datasource.key.parser;

import com.jn.langx.Parser;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

/**
 * 用于基于某个对象，解析出DataSourceKey。
 * 目前大致可以分两类：
 * 1) 基于javax.sql.DataSource对象的解析，这类parser通常在注册DataSource时使用，目的是为javax.sql.DataSource分配一个DataSourceKey。
 *
 * 2）基于java.lang.reflect.Method，java.lang.reflect.Class 的解析，这类parser通常在运行时，执行某个动作时进行解析。 例如Web开发中，可以在 Controller, Service, Dao 层都可以使用 @DataSource 注解。
 *
 *
 *
 * @param <I>
 *
 * @see DataSourceKeyDataSourceParser
 * @see DataSourceKeyAnnotationParser
 */
public interface DataSourceKeyParser<I> extends Parser<I, DataSourceKey> {
    @Override
    DataSourceKey parse(I input);
}
