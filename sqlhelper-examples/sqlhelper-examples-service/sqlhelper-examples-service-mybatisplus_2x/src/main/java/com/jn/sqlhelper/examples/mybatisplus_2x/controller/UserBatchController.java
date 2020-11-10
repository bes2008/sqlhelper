/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.examples.mybatisplus_2x.controller;


import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.examples.mybatisplus_2x.dao.UserDao;
import com.jn.sqlhelper.examples.model.User;
import com.jn.sqlhelper.mybatis.batch.MybatisBatchUpdaters;
import io.swagger.annotations.Api;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/users")
public class UserBatchController {

    private SqlSessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @PostMapping("/addUsers_JDBC_BATCH")
    public void addUsers_JDBC_BATCH(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserDao.class, "insert", BatchMode.JDBC_BATCH, users);
    }

    @PostMapping("/addUsers_BATCH_SQL")
    public void addUsers_BATCH_SQL(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserDao.class, "batchInsert", BatchMode.BATCH_SQL, users);
    }

    @PostMapping("/addUsers_SIMPLE")
    public void addUsers_SIMPLE(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserDao.class, "insert", BatchMode.SIMPLE, users);
    }

    @PostMapping("/addUsers_Guess")
    public void addUsers_Guess(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserDao.class, "insert", null, users);
    }


}
