package com.jn.sqlhelper.mybatisplus.spring.boot.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.jn.easyjson.core.util.JSONs;

public class MyBatisPlusPropertiesClone_GE_3_5_4 implements MyBatisPlusPropertiesClone{
    @Override
    public MybatisPlusProperties cloneObject(MybatisPlusProperties myBatisPlusProperties) {
        return JSONs.parse(JSONs.toJson(myBatisPlusProperties), MybatisPlusProperties.class);
    }
}
