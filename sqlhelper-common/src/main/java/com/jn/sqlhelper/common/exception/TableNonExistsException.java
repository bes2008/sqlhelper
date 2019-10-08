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

package com.jn.sqlhelper.common.exception;

public class TableNonExistsException extends RuntimeException {
    public TableNonExistsException() {
        super();
    }

    public TableNonExistsException(String message) {
        super(message);
    }

    public TableNonExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableNonExistsException(Throwable cause) {
        super(cause);
    }

}
