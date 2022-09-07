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

package com.jn.sqlhelper.datasource.key;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.invocation.matcher.MethodMatcher;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;

import java.lang.reflect.Method;

public class WriteOperationMethodMatcher implements MethodMatcher {
    private static final String DEFAULT_PATTERN_SET_EXPRESSION = "*write*;*insert*;*update*;*save*;*delete*;*modify*";
    private AntPathMatcher matcher;

    public WriteOperationMethodMatcher(@Nullable String patternExpression) {
        this(new AntPathMatcher(DEFAULT_PATTERN_SET_EXPRESSION, Emptys.isNotEmpty(patternExpression) ? patternExpression : DEFAULT_PATTERN_SET_EXPRESSION));
    }

    public WriteOperationMethodMatcher(AntPathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Boolean matches(Method method) {
        String methodName = method.getName();
        return matcher.matches(methodName);
    }

    @Override
    public Boolean matches(MethodInvocation methodInvocation) {
        return matches(methodInvocation.getJoinPoint());
    }

    @Override
    public boolean isInvocationMatcher() {
        return true;
    }
}
