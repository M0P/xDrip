package jamorham.keks.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * JamOrHam
 */

public class CertInfoRxMessage extends BaseMessage {

    public static final byte opcode = 0x0b;

    private int size = -1;
    private int which = -1;
    private int state = 0;

    public int getSize() {
        return size;
    }

    public int getWhich() {
        return which;
    }

    public int getState() {
        return state;
    }

    public boolean valid() {
        return (size > 0 && state == 0 && which >= 0);
    }

    public CertInfoRxMessage(final byte[] packet) {
        data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);;
        if (packet.length == 7) {
            if (data.get() == opcode) {
                state = data.get();
                which = data.get();
                size = data.getShort(); // might be an int but just ignore later bytes
            }
        }
    }
}
