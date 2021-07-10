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

import com.jn.langx.Nameable;
import com.jn.sqlhelper.common.transaction.TransactionDefinition;

public interface NamedTransactionDefinitionParser<I> extends TransactionDefinitionParser<I>, Nameable {
    @Override
    void setName(String s);

    @Override
    String getName();

    @Override
    TransactionDefinition parse(I i);
}
