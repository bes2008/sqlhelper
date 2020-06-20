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

package com.jn.sqlhelper.examples.springjdbc.controller;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;
import com.jn.sqlhelper.dialect.SqlRequest;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.model.User;
import com.jn.sqlhelper.springjdbc.JdbcTemplate;
import com.jn.sqlhelper.springjdbc.NamedParameterJdbcTemplate;
import com.jn.sqlhelper.springjdbc.resultset.SqlHelperRowMapperResultSetExtractor;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

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



    @GetMapping("/subqueryPagination_useMyBatis")
    public PagingResult subqueryPagination_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut) {
        User queryCondition = new User();
        queryCondition.setAge(10);
        queryCondition.setName("zhangsan_");
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        // request.setTenant(AndTenantBuilder.DEFAULT.column("TENANTID").value("2").build());
        request.subqueryPaging(true);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        List<User> users = userDao.selectByLimit_subqueryPagination(queryCondition);
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
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "count", required = false) boolean count,
            @RequestParam(name = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > 10[PAGING_END]) n where name like 'zhangsan%' ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > 10");
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
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
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

    @GetMapping("/_useSpringJdbc_args_rExecutor")
    public PagingResult list__useSpringJdbc_args_rExecutor(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);

        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > ?[PAGING_END]) n where name like CONCAT(?,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");

        Object[] args = testSubquery ? new Object[]{10, "zhangsan"} : new Object[]{10};
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), args, new ResultSetExtractor<List<User>>() {
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

    @GetMapping("/custom_BeanRowMapperTests")
    public PagingResult custom_BeanRowMapperTests(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        StringBuilder sqlBuilder = new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");

        BeanRowMapper<User> beanRowMapper = new BeanRowMapper(User.class);
        List<User> users = jdbcTemplate.query(sqlBuilder.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, 10);
            }
        }, new SqlHelperRowMapperResultSetExtractor<User>(beanRowMapper));
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        System.out.println(JSONBuilderProvider.simplest().toJson(users));
        return request.getResult();
    }

    @GetMapping("/_useSpringJdbcNamedTemplate")
    public PagingResult list_useSpringJdbcNamedTemplate(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > :age [PAGING_END]) n where name like CONCAT(:name,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > :age");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("age", 10);
        paramMap.put("name", "zhangsan");

        List<User> users = namedJdbcTemplate.query(sqlBuilder.toString(), paramMap, new RowMapper<User>() {
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



    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
    }


}
