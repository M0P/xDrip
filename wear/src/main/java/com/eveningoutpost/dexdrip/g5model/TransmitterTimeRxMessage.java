package com.eveningoutpost.dexdrip.g5model;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.services.G5CollectionService;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Created by jamorham on 25/11/2016.
 */

public class TransmitterTimeRxMessage extends BaseMessage {

    private final static String TAG = G5CollectionService.TAG; // meh

    public static final byte opcode = 0x25;
    private int status;
    private int currentTime;
    private int sessionStartTime;

    public int getStatus() {
        return status;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getSessionStartTime() {
        return sessionStartTime;
    }

    public TransmitterTimeRxMessage(byte[] packet) {
        if (packet.length >= 10) {
            data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            if (data.get() == opcode) {
                status = data.get();
                currentTime = data.getInt();
                sessionStartTime = data.getInt();
                if (sessionStartTime == -1) sessionStartTime = 0;
            }
        } else {
            UserError.Log.wtf(TAG, "Invalid TransmitterTimeRxMessage packet length: " + packet.length);
        }
    }

    public boolean sessionInProgress() {
        return sessionStartTime != 0 && sessionStartTime != -1;
    }

    public long getRealSessionStartTime() {
        if (sessionInProgress()) {
            return JoH.tsl() - ((long) (currentTime - sessionStartTime) * 1000L);
        } else {
            return 0;
        }
    }

    public long getSessionDuration() {
        if (sessionInProgress()) {
            return ((long) (currentTime - sessionStartTime)) * Constants.SECOND_IN_MS;
        } else {
            return 0;
        }
    }

}
