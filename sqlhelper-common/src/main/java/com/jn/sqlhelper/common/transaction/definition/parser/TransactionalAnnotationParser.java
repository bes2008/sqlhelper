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

package com.jn.sqlhelper.common.transaction.definition.parser;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.annotation.Transactional;
import com.jn.sqlhelper.common.transaction.TransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.RuleBasedTransactionDefinition;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class TransactionalAnnotationParser extends AbstractTransactionDefinitionAnnotationParser<Transactional> {
    @Override
    protected TransactionDefinition internalParse(AnnotatedElement annotatedElement, Transactional transactional) {
        RuleBasedTransactionDefinition txDef = new RuleBasedTransactionDefinition();
        String name;
        if (annotatedElement instanceof Method) {
            name = Reflects.getMethodString((Method) annotatedElement);
        } else {
            name = Reflects.getFQNClassName((Class) annotatedElement);
        }
        txDef.setName(name);
        txDef.setReadonly(transactional.readOnly());
        txDef.setIsolation(transactional.isolation());

        txDef.setRollbackRules(RuleBasedTransactionDefinition.buildRules(transactional.rollbackFor(), transactional.noRollbackFor()));

        return txDef;
    }

    @Override
    public Class<Transactional> getAnnotation() {
        return Transactional.class;
    }

}
