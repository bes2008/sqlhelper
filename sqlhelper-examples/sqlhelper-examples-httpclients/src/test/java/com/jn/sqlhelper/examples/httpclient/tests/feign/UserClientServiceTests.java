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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.examples.httpclients.feign.UserClientService;
import feign.Feign;
import feign.Target;
import feign.jackson.JacksonDecoder;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserClientServiceTests {

    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static Feign xxClient;
    private static JSON jsons = JSONBuilderProvider.simplest();

    @BeforeClass
    public static void init() {
        xxClient = Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .build();
    }

    @Test
    public void test() {
        Target<UserClientService> webTarget = new Target.HardCodedTarget(UserClientService.class, "userService", "http://localhost:8088/");
        UserClientService userClientService = xxClient.<UserClientService>newInstance(webTarget);
        System.out.println(jsons.toJson(userClientService.getUsers()));
    }

}
