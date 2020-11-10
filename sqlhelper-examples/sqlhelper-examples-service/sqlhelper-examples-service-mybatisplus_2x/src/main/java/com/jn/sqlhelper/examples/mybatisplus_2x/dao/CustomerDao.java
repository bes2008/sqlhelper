/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.examples.mybatisplus_2x.dao;

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
     * @param name like
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
