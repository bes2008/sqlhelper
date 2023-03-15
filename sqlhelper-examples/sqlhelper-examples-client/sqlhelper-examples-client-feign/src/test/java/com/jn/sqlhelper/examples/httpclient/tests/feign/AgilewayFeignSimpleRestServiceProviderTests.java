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

package com.jn.sqlhelper.examples.httpclient.tests.feign;

import com.jn.agileway.feign.HttpConnectionContext;
import com.jn.agileway.feign.HttpConnectionProperties;
import com.jn.agileway.feign.RestServiceProvider;
import com.jn.agileway.feign.StubProvider;
import com.jn.agileway.feign.supports.adaptable.ResponseBodyAdapter;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Types;
import com.jn.sqlhelper.examples.httpclients.feign.UserClientService2;
import feign.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AgilewayFeignSimpleRestServiceProviderTests {

    private static StubProvider provider;

    private static JSONFactory jsonFactory;

    @BeforeClass
    public static void init() {
        HttpConnectionProperties connectionProperties = new HttpConnectionProperties();
        connectionProperties.setNodes("localhost:8080");

        HttpConnectionContext context = new HttpConnectionContext();

        context.setConfiguration(connectionProperties);
        RestServiceProvider provider = new RestServiceProvider();
        jsonFactory = JsonFactorys.getJSONFactory(JsonScope.PROTOTYPE);
        provider.setContext(context);
        provider.setUnifiedRestResponseClass(RestRespBody.class);
        provider.setResponseBodyAdapter(new ResponseBodyAdapter() {
            @Override
            public Object adapt(Response response, Type type, Object o) {
                Class clazz = null;
                if (!Types.isClass(type) && Types.isParameterizedType(type)) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    clazz = (Class) parameterizedType.getRawType();
                } else {
                    clazz = (Class) type;
                }

                if (Reflects.isSubClassOrEquals(clazz, o.getClass())) {
                    return o;
                }
                return response.body().toString();
            }
        });
        AgilewayFeignSimpleRestServiceProviderTests.provider = provider;
        provider.init();
    }

     @Test
    public void testPagination() {
        System.out.println(jsonFactory.get().toJson(provider.getStub(UserClientService2.class).getUsers()));
    }

     @Test
    public void testGetById() {
        System.out.println(jsonFactory.get().toJson(provider.getStub(UserClientService2.class).getById("0001")));
    }

    @Test
    public void test404() {
        System.out.println(jsonFactory.get().toJson(provider.getStub(UserClientService2.class).getById_404("0001")));
    }

}
