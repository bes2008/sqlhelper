package com.fjn.helper.examples.mybatis.dao;

import com.fjn.helper.examples.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserDao {
    void insert(User user);
    void updateById(User user);
    void deleteById(String id);
    List<User> selectByLimit(User limit);
    User selectById(String id);
}
