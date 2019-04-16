package com.github.novotnyr.swixml.key;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.util.Optional;

public final class KeyBuilder {
    private KeyStore keyStore;

    public static KeyBuilder from(KeyStore keyStore) {
        KeyBuilder keyBuilder = new KeyBuilder();
        keyBuilder.keyStore = keyStore;

        return keyBuilder;
    }

    public EntryBuilder withAlias(String alias) {
        return new EntryBuilder(this.keyStore, alias);
    }

    public static class EntryBuilder {
        private KeyStore keyStore;

        private String alias;

        private KeyStore.ProtectionParameter keyProtectionParameter;

        public EntryBuilder(KeyStore keyStore, String alias) {
            this.keyStore = keyStore;
            this.alias = alias;
        }

        public EntryBuilder withKeyPassword(String password) {
            if (password == null) {
                return this;
            }
            this.keyProtectionParameter = new KeyStore.PasswordProtection(password.toCharArray());
            return this;
        }

        public Optional<PrivateKey> buildPrivateKey() {
            try {
                KeyStore.Entry entry = this.keyStore.getEntry(this.alias, this.keyProtectionParameter);
                if (entry == null) {
                    return Optional.empty();
                }
                if (entry instanceof KeyStore.PrivateKeyEntry) {
                    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
                    return Optional.ofNullable(privateKeyEntry.getPrivateKey());
                } else {
                    throw new KeystoreException("Requested keystore entry is not a private key, but " + entry.getClass());
                }
            } catch (NoSuchAlgorithmException e) {
                throw new KeystoreException("Keystore entry cannot be retrieved due to unsupported algorithm", e);
            } catch (UnrecoverableEntryException e) {
                throw new KeystoreException("Keystore entry cannot be retrieved. Are credentials correct?", e);
            } catch (KeyStoreException e) {
                throw new KeystoreException("Keystore has not been initialized", e);
            }
        }

        public Optional<Certificate> getCertificate() {
            try {
                Certificate certificate = this.keyStore.getCertificate(this.alias);
                return Optional.ofNullable(certificate);
            } catch (KeyStoreException e) {
                throw new KeystoreException("Keystore has not been initialized", e);
            }
        }
    }

}
