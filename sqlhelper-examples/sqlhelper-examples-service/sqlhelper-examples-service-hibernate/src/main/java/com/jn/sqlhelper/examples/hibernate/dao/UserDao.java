package com.jn.sqlhelper.examples.hibernate.dao;

import com.jn.sqlhelper.examples.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, String> {
}
