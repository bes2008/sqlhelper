/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.mybatis.mvc.repository;

import com.jn.sqlhelper.mybatis.mvc.entity.Entity;

import java.util.List;

public interface Repository<E extends Entity<ID>, ID> {
    void insert(E entity);

    void update(E entity);

    void delete(E entity);

    void merge(E entity);

    void deleteById(ID id);

    void updateById(E id);

    E selectById(ID id);

    void deleteByIds(List<ID> ids);

    List<E> selectByIds(List<ID> ids);

    void update(List<E> entities);

    List<E> selectAll();

    Integer selectCount();

    List<E> selectByLimit(E limit);

    Integer selectCountByLimit(E limit);

}
