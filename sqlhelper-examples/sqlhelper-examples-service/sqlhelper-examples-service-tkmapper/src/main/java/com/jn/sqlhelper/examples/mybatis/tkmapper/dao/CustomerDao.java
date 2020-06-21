package com.jn.sqlhelper.examples.mybatis.tkmapper.dao;

import com.jn.sqlhelper.examples.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerDao {
    void insert(Customer user);

    void updateById(Customer user);

    void deleteById(String id);

    List<Customer> selectByLimit(Customer limit);

    Customer selectById(String id);

    void batchInsert(List<Customer> users);
}
