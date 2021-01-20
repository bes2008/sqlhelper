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

package com.jn.sqlhelper.datasource.config;

import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.security.Ciphers;
import com.jn.langx.security.JCAEStandardName;
import com.jn.langx.security.PKIs;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.Charsets;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

public class DataSourcePropertiesRsaCipherer implements DataSourcePropertiesCipherer, Initializable {
    /**
     * base 64 public key
     */
    private String publicKeyString = "";

    private PublicKey publicKey;

    /**
     * base 64 private key
     */
    private String privateKeyString = "";

    private PrivateKey privateKey;

    public String getPublicKey() {
        return publicKeyString;
    }

    public void setPublicKey(String publicKey) {
        this.publicKeyString = publicKey;
    }

    public String getPrivateKey() {
        return privateKeyString;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKeyString = privateKey;
    }

    @Override
    public void init() throws InitializationException {
        if (Emptys.isNotEmpty(privateKeyString)) {
            this.privateKey = PKIs.createPrivateKey(JCAEStandardName.RSA.getName(), null, this.privateKeyString);
        }
        if (Emptys.isNotEmpty(publicKeyString)) {
            this.publicKey = PKIs.createPublicKey(JCAEStandardName.RSA.getName(), null, this.publicKeyString);
        }
    }

    @Override
    public String encrypt(@NotEmpty String text) {
        Preconditions.checkNotEmpty(text);
        Cipher cipher = Ciphers.createCipher(Ciphers.createAlgorithmTransformation("RSA"), null, Cipher.ENCRYPT_MODE, publicKey, null);
        byte[] encrypted = Ciphers.encrypt(cipher, text.getBytes(Charsets.UTF_8));
        return Base64.encodeBase64String(encrypted);
    }

    @Override
    public String decrypt(@NotEmpty String encryptedBase64Text) {
        Preconditions.checkNotEmpty(encryptedBase64Text);
        Cipher cipher = Ciphers.createCipher(Ciphers.createAlgorithmTransformation("RSA"), null, Cipher.DECRYPT_MODE, privateKey, null);
        byte[] originData = Ciphers.decrypt(cipher, Base64.decodeBase64(encryptedBase64Text));
        return new String(originData, Charsets.UTF_8);
    }
}
