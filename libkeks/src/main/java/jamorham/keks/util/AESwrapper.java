package jamorham.keks.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * JamOrHam
 */

public class AESwrapper {

    private final byte[] key;

    public AESwrapper(byte[] key) {
        this.key = key;
    }

    public byte[] aes(final byte[] plaintext) {
        try {
            Cipher aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return aesCipher.doFinal(plaintext, 0, plaintext.length);
        } catch (Exception e) {
            return null;
        }
    }

}
