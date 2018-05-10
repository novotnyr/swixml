package com.github.novotnyr.swixml.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeystoreBuilder {
    private char[] keystorePassword;

    private String type;

    private InputStream inputStream;

    public KeystoreBuilder withPassword(String password) {
        this.keystorePassword = password.toCharArray();
        return this;
    }

    public static KeystoreBuilder jks() {
        KeystoreBuilder builder = new KeystoreBuilder();
        builder.type = "JKS";
        return builder;
    }

    public KeystoreBuilder from(InputStream inputStream) throws FileNotFoundException {
        this.inputStream = inputStream;
        return this;
    }

    public KeystoreBuilder fromFile(File file) throws FileNotFoundException {
        this.inputStream = new FileInputStream(file);
        return this;
    }

    public KeyStore build() {
        try {
            KeyStore keyStore = KeyStore.getInstance(this.type);
            keyStore.load(this.inputStream, this.keystorePassword);
            return keyStore;
        } catch (KeyStoreException e) {
            throw new KeystoreException("Unsupported keystore type: " + this.type, e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeystoreException("Unsupported keystore algorithm type: " + this.type, e);
        } catch (CertificateException e) {
            throw new KeystoreException("Cannot load certificates", e);
        } catch (IOException e) {
            throw new KeystoreException("Cannot load keystore from source", e);
        }
    }
}
