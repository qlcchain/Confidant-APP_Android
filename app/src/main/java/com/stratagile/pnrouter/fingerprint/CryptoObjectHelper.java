package com.stratagile.pnrouter.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/// <summary>
/// Sample code for building a CryptoWrapper
/// </summary>
public class CryptoObjectHelper {
    // This can be key name you want. Should be unique for the app.
    static final String KEY_NAME = "ConfidantSecret";

    // We always use this keystore on Android.
    static final String KEYSTORE_NAME = "AndroidKeyStore";

    // Should be no need to change these values.
    static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING;
    final KeyStore _keystore;

    public CryptoObjectHelper() throws Exception {
        _keystore = KeyStore.getInstance(KEYSTORE_NAME);
        _keystore.load(null);
    }

    public FingerprintManager.CryptoObject buildCryptoObject() throws Exception {
        Cipher cipher = createCipher(true);
        return new FingerprintManager.CryptoObject(cipher);
    }

    Cipher createCipher(boolean retry) throws Exception {
        Key key = getOrCreateKeyStoreEntry();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            _keystore.deleteEntry(KEY_NAME);
            if (retry) {
                createCipher(false);
            } else {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        return cipher;
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private static SecretKey getOrCreateKeyStoreEntry() {
        if (hasKeyStoreEntry()) {
            return getKeyStoreEntry();
        } else {
            return createKeyStoreEntry();
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private static SecretKey createKeyStoreEntry() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            keyGenerator.init(keyGenParameterSpec);

            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new AssertionError(e);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private static SecretKey getKeyStoreEntry() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_NAME);
            keyStore.load(null);
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_NAME, null)).getSecretKey();
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private static boolean hasKeyStoreEntry() {
        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_NAME);
            ks.load(null);

            return ks.containsAlias(KEY_NAME) && ks.entryInstanceOf(KEY_NAME, KeyStore.SecretKeyEntry.class);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new AssertionError(e);
        }
    }

    Key GetKey() throws Exception {
        Key secretKey;
        if (!_keystore.isKeyEntry(KEY_NAME) || !_keystore.containsAlias(KEY_NAME) || !_keystore.entryInstanceOf(KEY_NAME, KeyStore.SecretKeyEntry.class)) {
            CreateKey();
        }

        secretKey = _keystore.getKey(KEY_NAME, null);
        return secretKey;
    }

    void CreateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}