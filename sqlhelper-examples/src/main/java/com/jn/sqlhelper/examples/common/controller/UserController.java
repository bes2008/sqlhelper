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

package com.jn.sqlhelper.examples.common.controller;

import com.github.pagehelper.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.examples.common.dao.UserDao;
import com.jn.sqlhelper.examples.common.model.User;
import com.jn.sqlhelper.springjdbc.JdbcTemplate;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequestMapping("/users")
public class UserController {
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostMapping
    public void add(User user) {
        userDao.insert(user);
    }

    @PutMapping("/{id}")
    public void update(String id, User user) {
        user.setId(id);
        User u = userDao.selectById(id);
        if (u == null) {
            add(user);
        } else {
            userDao.updateById(user);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) {
        userDao.deleteById(id);
    }

    @GetMapping("/_useSqlhelper_over_pageHelper")
    public Page list_sqlhelper_over_pageHelper(
            @RequestParam(name = "pageNo") Integer pageNo,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "sort") String sort) {

        Page page = PageHelper.startPage(pageNo, pageSize, sort);
        User queryCondition = new User();
        queryCondition.setAge(10);
        List<User> users = userDao.selectByLimit(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return page;
    }

    @GetMapping("/_useMyBatis")
    public PagingResult list_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        User queryCondition = new User();
        queryCondition.setAge(10);

        PagingRequest request = new PagingRequest().limit(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize).setOrderBy(SqlStyleOrderByBuilder.DEFAULT.build(sort));
        PagingRequestContextHolder.getContext().setPagingRequest(request);
        List<User> users = userDao.selectByLimit(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbc_rowMapper")
    public PagingResult list_useSpringJdbc_rowMapper(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        PagingRequest request = new PagingRequest().limit(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize).setOrderBy(SqlStyleOrderByBuilder.DEFAULT.build(sort));
        PagingRequestContextHolder.getContext().setPagingRequest(request);
        StringBuilder sqlBuilder = new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > 10");
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User u = new User();
                u.setId(rs.getString("ID"));
                u.setName(rs.getString("NAME"));
                u.setAge(rs.getInt("AGE"));
                return u;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbc_pSetter_rExecutor")
    public PagingResult list__useSpringJdbc_pSetter_rExecutor(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        PagingRequest request = new PagingRequest().limit(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize).setOrderBy(SqlStyleOrderByBuilder.DEFAULT.build(sort));
        PagingRequestContextHolder.getContext().setPagingRequest(request);
        StringBuilder sqlBuilder = new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, 10);
            }
        }, new ResultSetExtractor<List<User>>() {
            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getString("ID"));
                    u.setName(rs.getString("NAME"));
                    u.setAge(rs.getInt("AGE"));
                    users.add(u);
                }
                return users;
            }
        });
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        return request.getResult();
    }

    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
    }
}
