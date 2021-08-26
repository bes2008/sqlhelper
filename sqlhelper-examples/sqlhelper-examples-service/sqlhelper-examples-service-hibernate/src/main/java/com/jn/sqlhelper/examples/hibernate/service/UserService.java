package com.jn.sqlhelper.examples.hibernate.service;

import com.jn.sqlhelper.examples.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    void addUser(User user);

    void removeUser(String id);

    List<User> listUsers();

    Page<User> likeByName(String name, int pageNo, int pageSize);
}
