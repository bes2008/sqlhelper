package com.jn.sqlhelper.examples.hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.jn.sqlhelper.examples.hibernate",
        "com.jn.sqlhelper.examples.db.config",
        "com.jn.sqlhelper.examples.swagger.config",
        "com.jn.agileway.spring.utils"
})
public class SqlHelperHibernateApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlHelperHibernateApplication.class, args);
    }
}
