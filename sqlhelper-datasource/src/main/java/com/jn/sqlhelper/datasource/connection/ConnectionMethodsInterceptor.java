/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource.connection;

import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.invocation.aop.SparseMethodInterceptor;

import java.lang.reflect.Method;

public class ConnectionMethodsInterceptor extends SparseMethodInterceptor {
    @Override
    protected Object doIntercept(MethodInvocation methodInvocation) {
        return null;
    }

    @Override
    public boolean matches(Method method) {
        //method.getName()
        return false;
    }

    @Override
    public boolean matches(MethodInvocation methodInvocation) {
        return matches(methodInvocation.getJoinPoint());
    }

    @Override
    public boolean isInvocationMatcher() {
        return true;
    }
}
