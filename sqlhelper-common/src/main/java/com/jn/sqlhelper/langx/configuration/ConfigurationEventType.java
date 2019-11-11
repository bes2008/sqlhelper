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

package com.jn.sqlhelper.langx.configuration;

import com.jn.langx.Delegatable;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum ConfigurationEventType implements Delegatable<EnumDelegate> {
    ADD(0, "ADD", "add a configuration"),
    REMOVE(1, "REMOVE", "remove a configuration"),
    UPDATE(2, "UPDATE", "update a configuration");

    private EnumDelegate delegate;

    private ConfigurationEventType(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }


    @Override
    public EnumDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(EnumDelegate delegate) {
        this.delegate = delegate;
    }
}
