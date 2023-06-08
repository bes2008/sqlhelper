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

package com.jn.sqlhelper.common.security;

import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.security.crypto.JCAEStandardName;
import com.jn.langx.security.crypto.cipher.Ciphers;
import com.jn.langx.security.crypto.key.PKIs;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.Charsets;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 提供对 username, password 加密、解密功能
 *
 * @since 3.4.5
 */
public class DriverPropertiesRsaCipher implements DriverPropertiesCipher, Initializable {

    private String name;

    /**
     * base 64 public key
     */
    private String publicKeyString = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIn4lTUQa9uH7EzuNwa9mM2TxleZXhumfCiP9nMPBdn8zhp32B3PFKPs3hAtGtZ/153qX6bRrt1I/o1Oc1OH6QkCAwEAAQ==";

    private PublicKey publicKey;

    /**
     * base 64 private key
     */
    private String privateKeyString = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAifiVNRBr24fsTO43Br2YzZPGV5leG6Z8KI/2cw8F2fzOGnfYHc8Uo+zeEC0a1n/XnepfptGu3Uj+jU5zU4fpCQIDAQABAkAw49RDfHEr1otma1c2XULjPgUfj2oolCMU4IatGNqy9nW9BXgiHss09NnXIpNmnMQGZtpQ2U6Oct7SZY9JCsCBAiEA/y/yCC5/u3zg9zobIqWp58yLV6W2KLrlQoEy+42au5ECIQCKaRIhHeVzz2tsTGzuEa5pCsLdGpvxo4PJvyL6eQlp+QIhAJtyp2sYeDLLpXa0bKc0Z0WOsisYBNjW0KUsctQNtH4hAiA4gDXPYQXmpbiDaBtbf8pDxQnQ+mjIVmiY9baQqtIl+QIgCA0Q6ym9uGuq8hP3HxwvEE1XqppqykeorlYj8XXkWpI=";

    private PrivateKey privateKey;

    /**
     * RSA 算法目前应该只有这一个
     */
    private String transformation = "RSA/ECB/PKCS1Padding";

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

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
        if (publicKey == null) {
            init();
        }
        Cipher cipher = Ciphers.createCipher(Ciphers.createAlgorithmTransformation(transformation), null, Cipher.ENCRYPT_MODE, publicKey, null);
        byte[] encrypted = Ciphers.encrypt(cipher, text.getBytes(Charsets.UTF_8));
        return Base64.encodeBase64String(encrypted);
    }

    @Override
    public String decrypt(@NotEmpty String encryptedBase64Text) {
        Preconditions.checkNotEmpty(encryptedBase64Text);
        if (privateKey == null) {
            init();
        }
        Cipher cipher = Ciphers.createCipher(Ciphers.createAlgorithmTransformation(transformation), null, Cipher.DECRYPT_MODE, privateKey, null);
        byte[] originData = Ciphers.decrypt(cipher, Base64.decodeBase64(encryptedBase64Text));
        return new String(originData, Charsets.UTF_8);
    }

    @Override
    public String getName() {
        return JdbcSecuritys.DEFAULT_DRIVER_PROPERTIES_CIPHER_NAME;
    }
}
