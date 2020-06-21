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

package com.jn.sqlhelper.examples.apache.dbutils.controller;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Functions;
import com.jn.sqlhelper.apachedbutils.QueryRunner;
import com.jn.sqlhelper.apachedbutils.resultset.RowMapperResultSetHandler;
import com.jn.sqlhelper.apachedbutils.resultset.SingleRecordRowMapperResultSetHandler;
import com.jn.sqlhelper.apachedbutils.resultset.UpdatedRowsResultSetHandler;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.model.User;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private QueryRunner queryRunner;

    @Autowired
    @Qualifier("sqlMap")
    private Map<String, String> sqlMap;


    @PostMapping
    public void add(User user) throws Throwable {
        queryRunner.insert(
                sqlMap.get("user.insert"),
                new UpdatedRowsResultSetHandler(),
                user.getId(), user.getName(), user.getAge());
    }

    @PutMapping("/{id}")
    public void update(String id, User user) throws Throwable {
        user.setId(id);
        User u = getById(id);
        if (u == null) {
            add(user);
        } else {
            queryRunner.update(sqlMap.get("user.updateById"),
                    user.getId(), user.getName(), user.getAge(),
                    user.getId()
            );
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) throws Throwable {
        queryRunner.update(sqlMap.get("user.deleteById"), id);
    }

    @GetMapping("/users/_all")
    public List<User> all() throws Throwable{
        return queryRunner.query(sqlMap.get("user.selectAll"),
                new RowMapperResultSetHandler<User>(new BeanRowMapper<User>(User.class))
        );
    }

    @GetMapping("/users")
    public PagingResult list_useApacheDBUtils(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "testSubquery", required = false, defaultValue = "false") boolean testSubquery) throws SQLException {
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        if (testSubquery) {
            request.subqueryPaging(true);
        }
        StringBuilder sqlBuilder = testSubquery ? new StringBuilder("select * from ([PAGING_START]select ID, NAME, AGE from USER where 1=1 and age > ?[PAGING_END]) n where name like CONCAT(?,'%') ") : new StringBuilder("select ID, NAME, AGE from USER where 1=1 and age > ?");
        List<Object> params = Collects.emptyArrayList();
        params.add(10);
        if (testSubquery) {
            params.add("zhangsan");
        }

        List<User> users = queryRunner.query(sqlBuilder.toString(),
                new RowMapperResultSetHandler<User>(new BeanRowMapper<User>(User.class)),
                Collects.toArray(params)
        );

        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }


    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) throws Throwable {
        List<User> users = queryRunner.execute(
                sqlMap.get("user.selectById"),
                new SingleRecordRowMapperResultSetHandler<User>(new BeanRowMapper<User>(User.class)),
                id
        );
        return Collects.findFirst(users, Functions.nonNullPredicate());
    }

}
