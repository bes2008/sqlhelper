package com.jn.sqlhelper.springjdbc.spring.boot.autoconfigure;

import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.springjdbc.JdbcTemplatePaginationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "sqlhelper.springjdbc")
public class SpringJdbcTemplateProperties {
    @NestedConfigurationProperty
    private SQLInstrumentorConfig instrumentor = SQLInstrumentorConfig.DEFAULT;
    @NestedConfigurationProperty
    private JdbcTemplatePaginationProperties pagination = new JdbcTemplatePaginationProperties();
    @NestedConfigurationProperty
    private JdbcTemplateNativeProperties template = new JdbcTemplateNativeProperties();

    public SQLInstrumentorConfig getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLInstrumentorConfig instrumentor) {
        this.instrumentor = instrumentor;
    }

    public JdbcTemplatePaginationProperties getPagination() {
        return pagination;
    }

    public void setPagination(JdbcTemplatePaginationProperties pagination) {
        this.pagination = pagination;
    }

    public JdbcTemplateNativeProperties getTemplate() {
        return template;
    }

    public void setTemplate(JdbcTemplateNativeProperties template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "SpringJdbcTemplateProperties{" +
                "instrumentor=" + instrumentor +
                ", pagination=" + pagination +
                ", template=" + template +
                '}';
    }
}
