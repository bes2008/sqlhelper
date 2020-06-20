package com.jn.sqlhelper.examples.springjdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.jn.sqlhelper.examples.springjdbc",
        "com.jn.sqlhelper.examples.db.config"
})
public class SpringJdbcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringJdbcApplication.class, args);
    }
}
