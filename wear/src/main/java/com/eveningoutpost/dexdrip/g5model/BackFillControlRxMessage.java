package com.eveningoutpost.dexdrip.g5model;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * JamOrHam
 */

public class BackFillControlRxMessage extends BaseMessage {

    public static final byte opcode = 0x59;
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public BackFillControlRxMessage(final byte[] packet) {
        data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
        if ((data.get() == opcode)) {
            valid = true;
            // TODO more to parse here
        }
    }

}
