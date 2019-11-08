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

package com.jn.sqlhelper.langx.io.resource;

import com.jn.langx.io.resource.DefaultResourceLoader;
import com.jn.langx.io.resource.FileResource;
import com.jn.langx.io.resource.ResourceLoader;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.file.FileFilter;
import com.jn.langx.util.io.file.filter.ExistsFileFilter;
import com.jn.langx.util.io.file.filter.IsDirectoryFileFilter;
import com.jn.langx.util.io.file.filter.ReadableFileFilter;

import java.io.File;
import java.util.List;

public class DirectoryBasedFileResourceLoader implements ResourceLoader {
    private String directory;

    private ResourceLoader delegate;

    public DirectoryBasedFileResourceLoader(String directory) {
        this(directory, null);
    }

    public DirectoryBasedFileResourceLoader(String directory, ClassLoader classLoader) {
        Preconditions.checkNotNull(directory, "directory is null");
        Preconditions.checkTrue(new ExistsFileFilter().test(new File(directory)), "directory {} is not exists");
        Preconditions.checkTrue(new IsDirectoryFileFilter().test(new File(directory)), "directory {} is not a directory");
        Preconditions.checkTrue(new ReadableFileFilter().test(new File(directory)), "directory {} is not readable");
        this.directory = directory;

        this.delegate = new DefaultResourceLoader(classLoader);
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public FileResource loadResource(String filename) {
        return delegate.loadResource(directory + File.separator + filename);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public List<File> listFiles() {
        File dir = new File(directory);
        return Collects.asList(dir.listFiles());
    }

    public List<File> listFiles(FileFilter fileFilter) {
        File dir = new File(directory);
        return Collects.asList(dir.listFiles((java.io.FileFilter) fileFilter));
    }
}
