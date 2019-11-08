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

package com.jn.sqlhelper.langx.io.file;

import com.jn.langx.util.io.file.FileFilter;
import com.jn.langx.util.io.file.filter.AbstractFileFilter;
import com.jn.sqlhelper.langx.io.file.FileFilters;

import java.io.File;
import java.util.List;

public class AllFileFilter extends AbstractFileFilter {
    private FileFilter delegate;

    public AllFileFilter(List<? extends FileFilter> filters) {
        delegate = FileFilters.allFileFilter(filters);
    }

    @Override
    public boolean accept(File e) {
        return delegate.accept(e);
    }

    @Override
    public boolean accept(File dir, String name) {
        return delegate.accept(dir, name);
    }
}
