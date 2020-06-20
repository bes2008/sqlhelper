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

package com.jn.sqlhelper.examples.httpclients.feign;

import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.examples.httpclients.model.User;
import feign.Param;
import feign.RequestLine;

public interface UserClientService {
    @RequestLine("GET /users/_useMyBatis?pageSize=10&pageNo=1")
    public PagingResult<User> getUsers();

    @RequestLine("GET /users/_useMyBatis?pageSize=10&pageNo={pageNo}}")
    public PagingResult<User> queryUsers(@Param("pageNo") int pageNo);

    @RequestLine("GET /users/{id}")
    public User getById(@Param("id") String id);

    @RequestLine("POST /users")
    public void add(User user);

    @RequestLine("PATCH /users")
    public void update(User user);
}
