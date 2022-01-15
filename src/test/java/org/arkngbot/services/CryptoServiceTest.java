package org.arkngbot.services;

import com.google.crypto.tink.aead.AeadConfig;
import org.arkngbot.services.impl.CryptoServiceImpl;
import org.arkngbot.services.impl.PropertiesSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CryptoServiceTest {

    private static final String TEST_ENCRYPTION_KEY = "{\"primaryKeyId\":686584097,\"key\":[{\"keyData\":{\"typeUrl\":\"type.googleapis.com/google.crypto.tink.AesGcmKey\",\"value\":\"GiCq9BJhzf9tnxBC8aCi7P5jGuqhi4qksAmxbyr1xJOiDw==\",\"keyMaterialType\":\"SYMMETRIC\"},\"status\":\"ENABLED\",\"keyId\":686584097,\"outputPrefixType\":\"TINK\"}]}";
    private static final String ARKNGBOT_ENCRYPTIONKEY = "ENCRYPTIONKEY";
    private static final String TEST_PLAINTEXT = "[\n" +
            "  {\n" +
            "  \"number\": 1,\n" +
            "  \"answers\": [\"answer1\", \"answer2\"],\n" +
            "  \"nextQuest\": \"This is your next quest!\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"number\": 2,\n" +
            "    \"answers\": [\"answer3\", \"answer4\"],\n" +
            "    \"nextQuest\": \"This is your final quest!\"\n" +
            "  }\n" +
            "]";
    private static final String TEST_CIPHERTEXT = "0128EC7121524AADFD9BC200E5ED68F78C3E7E8094545A38AC5ADE62B8B6A3441B56D992E25CBCC96695ED0252CD73287D9900BA013AFCDB321F7CC821F3EEEDA4D5D54088A889350E593074114F61BBD6E693619C980F013B5FC3563B5BC1A5E22323C31766FEE5F65BB3EA976916DD463758CB73098DDC9E2B211794F670D2D7285896F49BBC63BE98EBAA9099909C2D01E648E480678C19ADF0CA778B26E552B99DB63460A9FAEB09121AA28E4797F9F15FF19CDD64844F68436777A0B38297B528259662A0D13046A00CCAB701192E1C3AFF85F2A26227C68EF14788FF3B46CA0D16F74A301BB5E8B090D358333935C84C46038167C8";

    private PropertiesSupport propertiesSupportMock;
    private CryptoService cryptoService;

    @BeforeEach
    public void setUp() {
        propertiesSupportMock = mock(PropertiesSupport.class);
        cryptoService = new CryptoServiceImpl(propertiesSupportMock);
        when(propertiesSupportMock.getConfigVariable(ARKNGBOT_ENCRYPTIONKEY)).thenReturn(TEST_ENCRYPTION_KEY);
    }

    @Test
    public void shouldEncrypt() throws Exception {
        AeadConfig.register();

        String ciphertext = cryptoService.encrypt(TEST_PLAINTEXT);

        // the resulting text is different each time, makes no sense to compare it to anything
        assertThat(ciphertext.length(), is(496));
    }

    @Test
    public void shouldDecrypt() throws Exception {
        AeadConfig.register();

        String plaintext = cryptoService.decrypt(TEST_CIPHERTEXT);

        assertThat(plaintext, is(TEST_PLAINTEXT));
    }
}
