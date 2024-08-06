package com.jn.sqlhelper.mybatisplus.spring.boot.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;

public interface MyBatisPlusPropertiesClone {
    MybatisPlusProperties cloneObject(MybatisPlusProperties myBatisPlusProperties);
}
