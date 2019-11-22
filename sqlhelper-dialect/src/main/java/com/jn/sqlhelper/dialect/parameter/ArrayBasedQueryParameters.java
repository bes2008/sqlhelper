
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

package com.jn.sqlhelper.dialect.parameter;

import com.jn.langx.util.collection.Collects;

import java.util.List;

public class ArrayBasedQueryParameters extends BaseQueryParameters<Object[]> {

    @Override
    public void setParameters(Object[] parameters, int beforeSubqueryCount, int afterSubqueryCount) {
        super.setParameters(parameters, beforeSubqueryCount, afterSubqueryCount);
        List<Object> all = Collects.asList(parameters);
        this.beforeSubqueryParameters = new Object[beforeSubqueryCount];
        for (int i = 0; i < beforeSubqueryCount; i++) {
            beforeSubqueryParameters[i] = all.remove(0);
        }
        int c = 0;
        this.afterSubqueryParameters = new Object[afterSubqueryCount];
        for (int i = all.size() - 1; i < afterSubqueryCount; i--) {
            afterSubqueryParameters[c] = all.remove(i);
            c++;
        }
        this.subqueryParameters = Collects.toArray(all);
    }
}
