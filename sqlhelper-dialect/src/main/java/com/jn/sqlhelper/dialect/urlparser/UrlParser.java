package com.jn.sqlhelper.dialect.urlparser;

import com.jn.langx.Named;

import java.util.List;

/**
 * 要求  getName() 返回值与 对应 的Dialect 的name 值一致
 */
public interface UrlParser extends Named {
    DatabaseInfo parse(final String url);

    List<String> getUrlSchemas();

}
