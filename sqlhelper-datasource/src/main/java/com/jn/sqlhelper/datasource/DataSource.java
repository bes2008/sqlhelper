package com.jn.sqlhelper.datasource;

public @interface DataSource {
    public String group() default DataSources.DATASOURCE_GROUP_DEFAULT;

    public String name() default "";
}
