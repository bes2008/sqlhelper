/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.examples.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = {
        "com.jn.sqlhelper.examples.mybatis",
        "com.jn.sqlhelper.examples.db.config",
        "com.jn.sqlhelper.examples.swagger.config",
        "com.jn.agileway.spring.utils"
})
@MapperScan("com.jn.sqlhelper.examples.mybatis.dao")
@ImportResource(locations = "classpath:/applicationContext-tx.xml")
public class MyBatisWithSpringBootTest {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisWithSpringBootTest.class, args);
    }
}
