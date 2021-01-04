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

package com.jn.sqlhelper.examples.mybatis.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.model.User;
import com.jn.sqlhelper.examples.mybatis.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserDao userDao;


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
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "countColumn", required = false) String countColumn) {

        Page page = PageHelper.offsetPage(pageNo, pageSize);
        // Page page = PageHelper.startPage(pageNo, pageSize, sort);
        page.setCountColumn(countColumn);
        User queryCondition = new User();
        queryCondition.setAge(10);
        List<User> users = userDao.selectByLimit(queryCondition);
        String json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        PageInfo pageInfo1 = new PageInfo(page);
        json = JSONBuilderProvider.simplest().toJson(pageInfo1);
        System.out.println(json);
        PageInfo pageInfo2 = new PageInfo(users);
        json = JSONBuilderProvider.simplest().toJson(pageInfo2);
        System.out.println(json);
        return page;
    }

    @GetMapping("/_useMyBatis")
    public PagingResult list_useMyBatis(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(value = "namelike", required = false) String namelike,
            @RequestParam(value = "namelikeNotUsingConcat", required = false, defaultValue = "false") boolean namelikeNotUsingConcat,
            @RequestParam(value = "testNonParameterLike", required = false, defaultValue = "false") boolean testNonParameterLike,
            @RequestParam(value = "likeEscapeEnabled", required = false, defaultValue = "true") boolean likeEscapeEnabled,
            @RequestParam(value = "grateAge", required = false, defaultValue = "10") int age
    ) {
        User queryCondition = new User();
        queryCondition.setAge(age);
        queryCondition.setName(namelike);
        List<User> users = null;
        if (namelikeNotUsingConcat) {
            users = userDao.selectByLimit_like2(queryCondition);
        }
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setEscapeLikeParameter(likeEscapeEnabled);
        System.out.println(JSONBuilderProvider.simplest().toJson(request));
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        if (testNonParameterLike) {
            users = userDao.selectByLimit_like3(queryCondition);
        } else {
            if (namelikeNotUsingConcat) {
                users = userDao.selectByLimit_like2(queryCondition);
            } else {
                users = userDao.selectByLimit(queryCondition);
            }
        }
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
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


    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
    }


    @GetMapping("/_ageIncrement")
    public PagingResult ageIncrement(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(value = "count", required = false) boolean count,
            @RequestParam(value = "useLastPageIfPageOut", required = false) boolean useLastPageIfPageOut,
            @RequestParam(value = "namelike", required = false) String namelike,
            @RequestParam(value = "namelikeNotUsingConcat", required = false, defaultValue = "false") boolean namelikeNotUsingConcat,
            @RequestParam(value = "testNonParameterLike", required = false, defaultValue = "false") boolean testNonParameterLike,
            @RequestParam(value = "likeEscapeEnabled", required = false, defaultValue = "true") boolean likeEscapeEnabled,
            @RequestParam(value = "grateAge", required = false, defaultValue = "10") int age
    ) {
        PagingResult list = list_useMyBatis(pageNo, pageSize, sort, count, useLastPageIfPageOut, namelike, namelikeNotUsingConcat, testNonParameterLike, likeEscapeEnabled, age);

        // age increment
        List<User> items = list.getItems();
        Collects.forEach(items, new Consumer<User>() {
            @Override
            public void accept(User user) {
                user.setAge(user.getAge() + 1);
                update(user.getId(), user);
            }
        });

        list = list_useMyBatis(pageNo, pageSize, sort, count, useLastPageIfPageOut, namelike, namelikeNotUsingConcat, testNonParameterLike, likeEscapeEnabled, age);
        return list;
    }

}
