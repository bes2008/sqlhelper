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

package com.jn.sqlhelper.examples.mybatis.mbg.controller;

import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import com.jn.sqlhelper.examples.model.User;
import com.jn.sqlhelper.examples.mybatis.mbg.model.UserExample;
import com.jn.sqlhelper.examples.mybatis.mbg.dao.UserMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/users")
public class UserController {
    private UserMapper userDao;


    @Autowired
    public void setUserMapper(UserMapper userDao) {
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


    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
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

        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAgeGreaterThanOrEqualTo(10)
                .andNameLike("%" + namelike + "%");

        PagingRequest request = SqlPaginations.preparePagination(pageNo == null ? 1 : pageNo, pageSize == null ? -1 : pageSize, sort);
        request.setEscapeLikeParameter(false);
        System.out.println(jsons.toJson(request));
        request.setCount(count);
        request.setUseLastPageIfPageOut(useLastPageIfPageOut);
        List<User> users = userDao.selectByExample(userExample);
        String json = jsons.toJson(request.getResult());
        System.out.println(json);
        json = jsons.toJson(users);
        System.out.println(json);
        return request.getResult();
    }



}
