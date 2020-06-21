package com.jn.sqlhelper.examples.mybatis.mbg.controller;


import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.examples.mybatis.mbg.dao.UserMapper;
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
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserMapper.class, "insert", BatchMode.JDBC_BATCH, users);
    }

    @PostMapping("/addUsers_BATCH_SQL")
    public void addUsers_BATCH_SQL(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserMapper.class, "batchInsert", BatchMode.BATCH_SQL, users);
    }

    @PostMapping("/addUsers_SIMPLE")
    public void addUsers_SIMPLE(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserMapper.class, "insert", BatchMode.SIMPLE, users);
    }

    @PostMapping("/addUsers_Guess")
    public void addUsers_Guess(@RequestBody List<User> users) throws Throwable {
        MybatisBatchUpdaters.batchUpdate(sessionFactory, UserMapper.class, "insert", null, users);
    }


}
