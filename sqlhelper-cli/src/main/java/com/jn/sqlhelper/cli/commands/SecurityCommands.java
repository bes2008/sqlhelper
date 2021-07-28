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

package com.jn.sqlhelper.cli.commands;

import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.JCAEStandardName;
import com.jn.langx.security.crypto.key.PKIs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.common.security.DriverPropertiesRsaCipher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.security.KeyPair;
import java.security.SecureRandom;

@ShellComponent
public class SecurityCommands {

    @ShellMethod(key = "gen-rsa", value = "generate rsa")
    public String genRSAKeyPair() {
        KeyPair keyPair = PKIs.createKeyPair(JCAEStandardName.RSA.getName(), (String) null, 512, new SecureRandom());
        String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        String privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        return StringTemplates.formatWithPlaceholder("publicKey: {}\nprivateKey:{}", publicKey, privateKey);
    }

    @ShellMethod(key = "encrypt", value = "encrypt with rsa")
    public String encrypt(
            @ShellOption(defaultValue = "__NULL__") String publicKey,
            String text) {
        DriverPropertiesRsaCipher cipherer = new DriverPropertiesRsaCipher();
        if (Emptys.isNotEmpty(publicKey)) {
            cipherer.setPublicKey(publicKey);
        }
        return cipherer.encrypt(text);
    }
}
