package jamorham.keks.message;

import static jamorham.keks.util.Util.getRandomKey;

import java.nio.ByteBuffer;

/**
 * JamOrHam
 */

public class SignChallengeTxMessage extends BaseMessage {

    byte opcode = 0x0c;
    byte[] challengeHash;

    public byte[] getChallengeHash() {
        return challengeHash;
    }

    public SignChallengeTxMessage(byte[] challenge) {
        challengeHash = challenge;
        data = ByteBuffer.allocate(17);
        data.put(opcode);
        data.put(challengeHash);
        byteSequence = data.array();
    }

    public SignChallengeTxMessage() {
        this(getRandomKey());
    }

}
