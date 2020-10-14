package com.jn.sqlhelper.examples.mybatis.dao;

import com.jn.sqlhelper.examples.model.Customer;
import org.apache.ibatis.annotations.Param;
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


    /**
     * 查询测试
     * @param namelike
     * @return
     */
    List<Customer> select1(@Param("name") String name);

    List<Customer> select2(@Param("name") String name, @Param("address") String address);

    /**
     * 更新测试
     * @param name
     */
    void updateTest1(@Param("city") String city ,@Param("name") String name);
}
