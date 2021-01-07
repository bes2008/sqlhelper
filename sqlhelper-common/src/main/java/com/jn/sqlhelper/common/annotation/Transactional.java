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

package com.jn.sqlhelper.common.annotation;

import com.jn.sqlhelper.common.transaction.utils.Isolation;

public @interface Transactional {
    int timeout() default -1; // units: second

    /**
     * Set this to true if the transaction should be only contain queries.
     */
    boolean readOnly() default false;

    /**
     * The Throwable's that will explicitly cause a rollback to occur.
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * The Throwable's that will explicitly NOT cause a rollback to occur.
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    Isolation isolation() default Isolation.DEFAULT;
}
