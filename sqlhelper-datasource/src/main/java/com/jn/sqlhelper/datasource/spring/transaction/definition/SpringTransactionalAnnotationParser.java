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

package com.jn.sqlhelper.datasource.spring.transaction.definition;

import com.jn.langx.util.Strings;
import com.jn.langx.util.enums.Enums;
import com.jn.sqlhelper.common.transaction.Isolation;
import com.jn.sqlhelper.common.transaction.definition.DefaultTransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.parser.AbstractTransactionDefinitionAnnotationParser;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.AnnotatedElement;

public class SpringTransactionalAnnotationParser extends AbstractTransactionDefinitionAnnotationParser<Transactional> {
    @Override
    protected TransactionDefinition internalParse(AnnotatedElement annotatedElement, Transactional transactional) {
        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        txDef.setName(Strings.join(",", new String[]{transactional.propagation().name(), transactional.isolation().name(), "Readonly=" + transactional.readOnly()}));
        txDef.setReadonly(transactional.readOnly());
        txDef.setIsolation(Enums.ofCode(Isolation.class, transactional.isolation().value()));
        txDef.setRollbackFor(transactional.rollbackFor());
        txDef.setNoRollbackFor(transactional.noRollbackFor());
        return txDef;
    }

    @Override
    public Class<Transactional> getAnnotation() {
        return Transactional.class;
    }
}
