package com.jn.sqlhelper.mybatis.solon.plugin;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //马上加载
        app.loadBean(SqlHelperMybatisConfiguration.class);
    }
}
