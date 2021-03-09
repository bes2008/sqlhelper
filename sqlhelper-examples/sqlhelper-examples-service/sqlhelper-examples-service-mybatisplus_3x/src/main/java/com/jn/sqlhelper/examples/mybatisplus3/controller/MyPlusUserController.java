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

package com.jn.sqlhelper.examples.mybatisplus3.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jn.sqlhelper.examples.model.MyPlusUser;
import com.jn.sqlhelper.examples.mybatisplus3.dao.MyPlusUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: mybatis-plus demo controller
 * @Author: singo
 * @Date: 2021.03.09 10:36
 */
@RestController
@RequestMapping("/myplususer")
public class MyPlusUserController {
    private MyPlusUserDao myPlusUserDao;

    @Autowired
    public void setMyPlusUserDao(MyPlusUserDao myPlusUserDao) {
        this.myPlusUserDao = myPlusUserDao;
    }

    @PostMapping
    public void add(MyPlusUser myPlusUser) {
        myPlusUserDao.insert(myPlusUser);
    }

    @PutMapping("/{id}")
    public void update(String id, MyPlusUser myPlusUser) {
        myPlusUser.setId(id);
        MyPlusUser u = myPlusUserDao.selectById(id);
        if (u == null) {
            add(u);
        } else {
            myPlusUserDao.updateById(myPlusUser);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id) {
        myPlusUserDao.deleteById(id);
    }

    @GetMapping("getPageList")
    public PageInfo<MyPlusUser> getPageList(MyPlusUser record,
                                            HttpServletRequest req) {
        PageHelper.startPage(1, 10);
        return new PageInfo<>(myPlusUserDao.selectList(Wrappers.emptyWrapper()));
    }
}
