
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

package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.ClassLoaders;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

public class DerbyDialect extends AbstractDialect {
    private int driverVersionMajor;
    private int driverVersionMinor;

    public DerbyDialect() {
        super();
        determineDriverVersion();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
    }

    private void determineDriverVersion() {
        try {
            ClassLoaders.loadClass("org.apache.derby.tools.sysinfo", DerbyDialect.class.getClassLoader());
        } catch (Exception e) {
            this.driverVersionMajor = -1;
            this.driverVersionMinor = -1;
        }
    }

    private boolean isTenPointFiveReleaseOrNewer() {
        return (this.driverVersionMajor > 10) || ((this.driverVersionMajor == 10) && (this.driverVersionMinor >= 5));
    }

    @Override
    public boolean isSupportsLimit() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
