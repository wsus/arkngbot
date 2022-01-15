package org.arkngbot.services.impl;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import org.arkngbot.services.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String ARKNGBOT_ENCRYPTIONKEY = "ENCRYPTIONKEY";

    private PropertiesSupport propertiesSupport;

    @Autowired
    public CryptoServiceImpl(PropertiesSupport propertiesSupport) {
        this.propertiesSupport = propertiesSupport;
    }

    @NonNull
    @Override
    public String encrypt(@NonNull String plaintext) throws GeneralSecurityException, IOException {
        String keyAsString = propertiesSupport.getConfigVariable(ARKNGBOT_ENCRYPTIONKEY);
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withString(keyAsString));
        Aead aead = keysetHandle.getPrimitive(Aead.class);
        byte[] encryptedBytes = aead.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), null);
        return DatatypeConverter.printHexBinary(encryptedBytes);
    }

    @NonNull
    @Override
    public String decrypt(@NonNull String ciphertext) throws GeneralSecurityException, IOException {
        String keyAsString = propertiesSupport.getConfigVariable(ARKNGBOT_ENCRYPTIONKEY);
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withString(keyAsString));
        Aead aead = keysetHandle.getPrimitive(Aead.class);

        byte[] decryptedBytes = aead.decrypt(DatatypeConverter.parseHexBinary(ciphertext), null);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
