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

package com.jn.sqlhelper.batchinsert;

public class BatchInsertResult {
    private final String time;
    private final int realInsertNum;
    private final int expectResult;

    public BatchInsertResult(String time, int expectResult, int realInsertNum) {
        this.time = time;
        this.expectResult = expectResult;
        this.realInsertNum = realInsertNum;
    }

    public String getTime() {
        return time;
    }

    public int getRealInsertNum() {
        return realInsertNum;
    }

    public int getExpectResult() {
        return expectResult;
    }
}
