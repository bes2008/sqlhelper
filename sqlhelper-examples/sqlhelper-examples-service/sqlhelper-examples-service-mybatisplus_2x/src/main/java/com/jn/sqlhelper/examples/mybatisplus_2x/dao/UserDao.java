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

import com.jn.sqlhelper.examples.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    void insert(User user);

    void updateById(User user);

    void deleteById(String id);

    List<User> selectByLimit(User limit);

    /**
     * 测试 '%' #{property} '%' 场景
     * @param limit
     * @return
     */
    List<User> selectByLimit_like2(User limit);

    List<User> selectByLimit_like3(User limit);

    List<User> selectByLimit_subqueryPagination(User limit);

    User selectById(String id);

    void batchInsert(List<User> users);
}
