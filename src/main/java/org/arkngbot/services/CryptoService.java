package org.arkngbot.services;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Contains methods for string encryption and decryption.
 */
public interface CryptoService {

    /**
     * Encrypts the given string.
     * @param plaintext the plaintext string to encrypt
     * @return the encrypted string
     */
    @NonNull
    String encrypt(@NonNull String plaintext) throws GeneralSecurityException, IOException;

    /**
     * Decrypts the given string.
     * @param ciphertext the ciphertext string to decrypt
     * @return the decrypted string
     */
    @NonNull
    String decrypt(@NonNull String ciphertext) throws GeneralSecurityException, IOException;
}
