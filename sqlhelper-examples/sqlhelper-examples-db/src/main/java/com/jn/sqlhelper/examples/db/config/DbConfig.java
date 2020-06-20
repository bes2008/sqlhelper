package com.jn.sqlhelper.examples.db.config;

import com.jn.langx.io.resource.Resources;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.collection.Collects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class DbConfig {

    @Bean("sqlMap")
    public Map<String, String> sqlMap() {
        try {
            return Collects.propertiesToStringMap(Props.load(Resources.loadClassPathResource("sqlscripts/sql.properties")));
        } catch (IOException ex) {
            return Collects.emptyHashMap();
        }
    }

}
