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

import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.examples.common.dao.UserDao;
import com.jn.sqlhelper.examples.common.model.User;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public PagingResult list(
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name="sort",required = false) String sort ) {
        User queryCondtion = new User();
        queryCondtion.setAge(10);

        PagingRequest request = new PagingRequest().limit(pageNo == null ? 1 : pageNo, pageSize==null ? -1 : pageSize).setOrderBy(SqlStyleOrderByBuilder.DEFAULT.build(sort));
        PagingRequestContextHolder.getContext().setPagingRequest(request);
        List<User> users = userDao.selectByLimit(queryCondtion);
        request.getResult().setItems(users);
        return request.getResult();
    }

    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id) {
        return userDao.selectById(id);
    }
}
