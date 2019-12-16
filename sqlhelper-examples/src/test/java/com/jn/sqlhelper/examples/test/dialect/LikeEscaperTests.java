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

package com.jn.sqlhelper.examples.test.dialect;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.dialect.internal.likeescaper.CStyleEscaper;
import org.junit.Test;

import java.util.List;

public class LikeEscaperTests {
    @Test
    public void testEscapeParameters() {
        final List<String> parameters = Collects.asList("a",
                "ab",
                " a_b",
                "_a b",
                "%a_b_"
        );

        final CStyleEscaper likeEscaper = new CStyleEscaper();
        Collects.forEach(parameters, new Consumer2<Integer, String>() {
            @Override
            public void accept(Integer index, String value) {
                parameters.set(index, likeEscaper.escape(value));
                System.out.println(parameters.get(index));
            }
        });


    }
}
