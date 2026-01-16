package com.eveningoutpost.dexdrip.g5model;

import com.eveningoutpost.dexdrip.services.G5CollectionService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Created by jamorham on 25/11/2016.
 */

public class SessionStartTxMessage extends BaseMessage {

    private final static String TAG = G5CollectionService.TAG; // meh

    public static final byte opcode = 0x26;
    private final int dexTime;
    private final long startTime;

    public int getDexTime() {
        return dexTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public SessionStartTxMessage(long startTime, int dexTime) {
        this.startTime = startTime;
        this.dexTime = dexTime;
        init(opcode, 5);
        data.putInt(dexTime);
        byteSequence = data.array();
    }

    public SessionStartTxMessage(long startTime, int dexTime, String code) {
        this.startTime = startTime;
        this.dexTime = dexTime;
        init(opcode, 9);
        data.putInt(dexTime);
        G6CalibrationParameters params = new G6CalibrationParameters(code);
        if (params.isValid()) {
            data.putShort((short) params.getParamA());
            data.putShort((short) params.getParamB());
        } else {
            throw new RuntimeException("Invalid sensor code: " + code);
        }
        byteSequence = data.array();
    }

    // parsing for re-construct
    public SessionStartTxMessage(byte[] packet) {
        data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
        // TODO opcode check
        data.get();
        dexTime = data.getInt();
        startTime = 0; // unknown when reconstructing
    }

}
