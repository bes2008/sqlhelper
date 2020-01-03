/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.mybatis.plugins;

public class ExecutorInvocationPipeline {
    private ExecutorInvocationHandler head;
    private ExecutorInvocationHandler tail;

    public void addFirstHandler(ExecutorInvocationHandler handler) {
        if (head == null && tail == null) {
            head = handler;
            tail = handler;
        } else {
            handler.setNext(head);
            head = handler;
        }
    }

    public void addLastHandler(ExecutorInvocationHandler handler) {
        if (head == null && tail == null) {
            head = handler;
            tail = handler;
        } else {
            tail.setNext(handler);
            tail = handler;
        }
    }

    public void handle(ExecutorInvocation invocation) {

    }
}
