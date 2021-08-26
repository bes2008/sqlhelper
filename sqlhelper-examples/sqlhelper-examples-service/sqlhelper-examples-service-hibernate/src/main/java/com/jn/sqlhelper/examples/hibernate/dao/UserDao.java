package com.jn.sqlhelper.examples.hibernate.dao;

import com.jn.sqlhelper.examples.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, String> {
    @Query(value = "select u.id as id, u.name as name, u.age as age from User u where u.name like CONCAT(CONCAT('%',:name),'%') \n-- #pageable\n",
            countQuery ="select count(*) from User u where u.name like CONCAT(CONCAT('%',:name),'%')",
            nativeQuery = true )
    public Page<User> findByLimit(@Param("name") String name, Pageable pageable);
}
