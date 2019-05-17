
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

package com.github.fangjinuo.sqlhelper.dialect.internal.limit;

import com.github.fangjinuo.sqlhelper.dialect.Dialect;
import com.github.fangjinuo.sqlhelper.dialect.RowSelection;

public class LimitHelper {
    public static boolean hasMaxRows(final RowSelection selection) {
        return selection != null && selection.getLimit() != null && selection.getLimit() > 0;
    }

    public static boolean useLimit(final Dialect dialect, final RowSelection selection) {
        return dialect != null && dialect.isSupportsLimit() && hasMaxRows(selection);
    }

    public static boolean hasFirstRow(final RowSelection selection) {
        return getFirstRow(selection) > 0;
    }

    public static int getFirstRow(final RowSelection selection) {
        return (selection == null || selection.getOffset() == null) ? 0 : ((int) selection.getOffset());
    }

    private LimitHelper() {
    }
}
