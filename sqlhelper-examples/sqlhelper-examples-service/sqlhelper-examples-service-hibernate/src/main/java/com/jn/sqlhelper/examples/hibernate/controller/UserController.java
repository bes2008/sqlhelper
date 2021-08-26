package com.jn.sqlhelper.examples.hibernate.controller;

import com.jn.sqlhelper.examples.hibernate.service.UserService;
import com.jn.sqlhelper.examples.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void addUser(User user){
        userService.addUser(user);
    }

    @GetMapping
    public List<User> listAll(){
        return userService.listUsers();
    }
}
