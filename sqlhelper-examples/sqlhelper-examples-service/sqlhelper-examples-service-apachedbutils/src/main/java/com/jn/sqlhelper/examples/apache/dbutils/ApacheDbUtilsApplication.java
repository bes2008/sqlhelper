package com.jn.sqlhelper.examples.apache.dbutils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.jn.sqlhelper.examples.apache.dbutils",
        "com.jn.sqlhelper.examples.db.config",
        "com.jn.sqlhelper.examples.swagger.config"
})
public class ApacheDbUtilsApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ApacheDbUtilsApplication.class, args);
            Thread.sleep(10000000);
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }
}
