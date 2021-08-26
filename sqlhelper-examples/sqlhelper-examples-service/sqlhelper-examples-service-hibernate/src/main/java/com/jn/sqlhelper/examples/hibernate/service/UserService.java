package com.jn.sqlhelper.examples.hibernate.service;

import com.jn.sqlhelper.examples.model.User;

import java.util.List;

public interface UserService {
    void addUser(User user);

    void removeUser(String id);

    List<User> listUsers();
}
