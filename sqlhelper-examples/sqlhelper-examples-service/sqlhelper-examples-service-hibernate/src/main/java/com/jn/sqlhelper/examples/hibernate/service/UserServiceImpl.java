package com.jn.sqlhelper.examples.hibernate.service;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.examples.hibernate.dao.UserDao;
import com.jn.sqlhelper.examples.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addUser(User user) {
        userDao.save(user);
    }

    @Override
    public void removeUser(String id) {
        userDao.delete(id);
    }

    @Override
    public List<User> listUsers() {
        return Collects.asList(userDao.findAll());
    }
}
