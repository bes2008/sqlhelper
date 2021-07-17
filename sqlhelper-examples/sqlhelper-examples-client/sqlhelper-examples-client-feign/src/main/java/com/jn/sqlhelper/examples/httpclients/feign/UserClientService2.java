/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.examples.httpclients.feign;


import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.examples.httpclients.model.User;
import feign.Param;
import feign.RequestLine;

public interface UserClientService2 {
    @RequestLine("GET /users?pageSize=10&pageNo=1")
    public TestRestBody<PagingResult<User>> getUsers();

    @RequestLine("GET /users?pageSize=10&pageNo={pageNo}}")
    public TestRestBody<PagingResult<User>> queryUsers(@Param("pageNo") int pageNo);

    @RequestLine("GET /users/{id}")
    public TestRestBody<User> getById(@Param("id") String id);

    /**
     * 测试 404
     * @param id
     * @return
     */
    @RequestLine("GET /users2/{id}")
    public TestRestBody<User> getById_404(@Param("id") String id);

    @RequestLine("POST /users")
    public void add(User user);

    @RequestLine("PATCH /users")
    public void update(User user);
}
