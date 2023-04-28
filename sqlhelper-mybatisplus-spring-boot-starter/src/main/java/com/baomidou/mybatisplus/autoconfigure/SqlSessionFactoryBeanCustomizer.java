package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

/**
 * 该接口在 mybatis-plus 3.5.3 版本才会有，为了保证 DynamicSqlSessionTemplateAutoConfiguration 能够 兼容 mybatis-plus 3.5.3 之前的版本，
 * 先将该接口 copy 一份
 *
 * @since sqlhelper 4.0.6
 * @since mybatis-plus 3.5.3
 */
public interface SqlSessionFactoryBeanCustomizer {

    /**
     * Customize the given a {@link MybatisSqlSessionFactoryBean} object.
     *
     * @param factoryBean the factory bean object to customize
     */
    void customize(MybatisSqlSessionFactoryBean factoryBean);
}
