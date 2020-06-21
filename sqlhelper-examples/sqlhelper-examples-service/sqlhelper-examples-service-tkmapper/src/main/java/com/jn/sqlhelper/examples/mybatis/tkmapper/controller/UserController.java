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

package com.jn.sqlhelper.examples.mybatis.tkmapper.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.model.User;
import com.jn.sqlhelper.examples.mybatis.tkmapper.dao.UserDao;
import io.swagger.annotations.Api;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;

@Api
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
        User u = userDao.selectByPrimaryKey(id);
        if (u == null) {
            add(user);
        } else {
            userDao.updateByPrimaryKey(user);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) {
        userDao.deleteByPrimaryKey(id);
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
        PageHelper.orderBy(" id desc");

        WeekendSqls weekendSqls = WeekendSqls.custom()
                .andLike("name", "%zhangsan%")
                .andGreaterThanOrEqualTo("age", 10);
        Example example = Example.builder(User.class).setDistinct(false).where(weekendSqls).build();

        JSON jsons = JSONBuilderProvider.simplest();

        List<User> users = userDao.selectByExample(example);
        String json = jsons.toJson(users);
        System.out.println(json);
        json = jsons.toJson(users);
        System.out.println(json);
        PageInfo pageInfo1 = new PageInfo(page);
        json = jsons.toJson(pageInfo1);
        System.out.println(json);
        PageInfo pageInfo2 = new PageInfo(users);
        json = jsons.toJson(pageInfo2);
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
            @RequestParam(value = "namelike", required = false, defaultValue = "") String namelike,
            @RequestParam(value = "grateAge", required = false, defaultValue = "10") int age,
            @RequestParam(value = "testTenant", required = false, defaultValue = "false") boolean testTenant,
            @RequestParam(value = "tenantId", required = false, defaultValue = "1") String tenantId) {

        JSON jsons = JSONBuilderProvider.simplest();

        User queryCondition = new User();
        queryCondition.setAge(age);
        queryCondition.setName(namelike);


        WeekendSqls weekendSqls = WeekendSqls.custom()
                .andLike("name", "%" + namelike + "%")
                .andGreaterThanOrEqualTo("age", age);
        Example example = Example.builder(User.class).setDistinct(false).where(weekendSqls).build();

        // 正常查询
        List<User> users = userDao.selectByExample(example);
        String json = jsons.toJson(users);
        System.out.println("正常查询");
        System.out.println(json);
        System.out.println("=====================================");
        // 用 RowBounds分页查询
        users = userDao.selectByExampleAndRowBounds(example, new RowBounds(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize));
        json = jsons.toJson(users);
        System.out.println("使用row bounds 查询");
        System.out.println(json);
        System.out.println("=====================================");

        // 使用 SqlHelper API 分页查询
        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setEscapeLikeParameter(false);
        users = userDao.selectByExample(example);
        System.out.println("使用 SqlHelper API 分页查询");
        System.out.println(jsons.toJson(request));
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        json = jsons.toJson(request.getResult());
        System.out.println(json);

        json = jsons.toJson(users);
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
        request.subqueryPaging(true);
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        int limit = pageSize == null ? -1 : pageSize;
        int offset = ((pageNo == null ? 1 : pageNo) - 1) * limit;
        List<User> users = userDao.selectByExampleAndRowBounds(queryCondition, new RowBounds(offset, limit));
        String json = JSONBuilderProvider.simplest().toJson(request.getResult());
        System.out.println(json);
        json = JSONBuilderProvider.simplest().toJson(users);
        System.out.println(json);
        return request.getResult();
    }


    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectByPrimaryKey(id);
    }

}
