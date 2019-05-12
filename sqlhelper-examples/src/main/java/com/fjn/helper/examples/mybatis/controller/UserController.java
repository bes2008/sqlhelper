package com.fjn.helper.examples.mybatis.controller;

import com.fjn.helper.examples.model.User;
import com.fjn.helper.examples.mybatis.dao.UserDao;
import com.fjn.helper.sql.dialect.pagination.PagingContextHolder;
import com.fjn.helper.sql.dialect.pagination.PagingRequest;
import com.fjn.helper.sql.dialect.pagination.PagingResult;
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
    public void add(User user){
        userDao.insert(user);
    }

    @PutMapping("/{id}")
    public void update(String id, User user){
        user.setId(id);
        User u = userDao.selectById(id);
        if(u==null){
            add(user);
        }else {
            userDao.updateById(user);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestParam("id") String id){
        userDao.deleteById(id);
    }

    @GetMapping
    public PagingResult list(){
        User queryCondtion = new User();
        queryCondtion.setAge(10);
        PagingRequest request = new PagingRequest()
                .setPageNo(1)
                .setPageSize(10);
        PagingContextHolder.getContext().setPagingRequest(request);
        List<User> users = userDao.selectByLimit(queryCondtion);
        request.getResult().setItems(users);
        return request.getResult();
    }

    @GetMapping("/{id}")
    public User getById(@RequestParam("id") String id){
        return userDao.selectById(id);
    }
}
