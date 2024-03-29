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

import com.jn.sqlhelper.common.security.DriverPropertiesRsaCipher;
import org.junit.Assert;
import org.junit.Test;

public class RsaCiphererTest {
    @Test
    public void test() {
        String username = "admin";
        DriverPropertiesRsaCipher cipherer = new DriverPropertiesRsaCipher();
        cipherer.init();
        String encrypted = cipherer.encrypt(username);
        String username2 = cipherer.decrypt(encrypted);
        Assert.assertEquals(username, username2);

        encrypted = "" + (encrypted.charAt(0) + 1) + encrypted.substring(1);
        String username3 = cipherer.decrypt(encrypted);
        Assert.assertEquals(username, username3);

        encrypted = "admin3";
        String username4 = cipherer.decrypt(encrypted);
        Assert.assertEquals(username, username4);
    }
}
