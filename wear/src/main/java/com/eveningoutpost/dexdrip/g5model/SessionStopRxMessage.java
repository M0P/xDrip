package com.eveningoutpost.dexdrip.g5model;

import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.services.G5CollectionService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Created by jamorham on 25/11/2016.
 */

public class SessionStopRxMessage extends BaseMessage {

    private final static String TAG = G5CollectionService.TAG; // meh

    public static final byte opcode = 0x29;
    private int status;
    private int sessionStop;
    private int sessionStart;
    private int transmitterTime;

    public int getStatus() {
        return status;
    }

    public int getSessionStop() {
        return sessionStop;
    }

    public int getSessionStart() {
        return sessionStart;
    }

    public int getTransmitterTime() {
        return transmitterTime;
    }

    public SessionStopRxMessage(byte[] packet, String transmitterId) {
        if (packet.length >= 2) {
            data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            if (data.get() == opcode) {
                status = data.get();
                if (status == 0x28) {
                    sessionStop = data.getInt();
                    sessionStart = data.getInt();
                    transmitterTime = data.getInt();
                } else {
                    UserError.Log.e(TAG, "Session Stop Failed status: " + status);
                }
            }
        }
    }

    public boolean isValid() {
        return (status == 0x28);
    }

    public boolean isOkay() {
        return isValid();
    }

    public long getSessionStart() {
        return DexTimeKeeper.fromDexTimeCached(sessionStart);
    }

    public long getSessionStop() {
        return DexTimeKeeper.fromDexTimeCached(sessionStop);
    }

}
