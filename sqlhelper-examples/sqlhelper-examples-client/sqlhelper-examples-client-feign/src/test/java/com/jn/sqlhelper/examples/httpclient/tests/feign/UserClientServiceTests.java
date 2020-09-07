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

package com.jn.sqlhelper.examples.httpclient.tests.feign;

import com.jn.agileway.feign.codec.EasyjsonDecoder;
import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilder;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.sqlhelper.examples.httpclients.feign.UserClientService;
import feign.Feign;
import feign.Target;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserClientServiceTests {

    private static JSONFactory jsonFactory;
    private static Feign feign;
    private static UserClientService userClientService;
    private static JSON jsons;

    static {
        JSONBuilder jsonBuilder = JSONBuilderProvider.create().enableIgnoreAnnotation();
        jsonFactory = JsonFactorys.getJSONFactory(jsonBuilder, JsonScope.SINGLETON);
        jsons = jsonFactory.get();
    }

    @BeforeClass
    public static void init() {
        feign = Feign.builder()
                .decoder(new EasyjsonDecoder(jsonFactory))
                .build();
        Target<UserClientService> webTarget = new Target.HardCodedTarget(UserClientService.class, "userService", "http://localhost:8080/");
        userClientService = feign.<UserClientService>newInstance(webTarget);
    }

    @Test
    public void testPagination() {
        System.out.println(jsons.toJson(userClientService.getUsers()));
    }

    @Test
    public void testGetById() {
        System.out.println(jsons.toJson(userClientService.getById("0001")));
    }

}
