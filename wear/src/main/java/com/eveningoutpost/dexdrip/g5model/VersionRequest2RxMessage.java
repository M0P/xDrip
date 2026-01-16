package com.eveningoutpost.dexdrip.g5model;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;


/**
 * Created by jamorham on 25/11/2016.
 */

public class VersionRequest2RxMessage extends BaseMessage {

    public static final byte opcode = 0x53;
    public static final byte opcode2 = 0x54;

    private int status;
    private int warmupSeconds;
    private int hardExpirationDays;
    private int typicalSensorDays;
    private boolean type2;

    public int getStatus() {
        return status;
    }

    public int getWarmupSeconds() {
        return warmupSeconds;
    }

    public int getHardExpirationDays() {
        return hardExpirationDays;
    }

    public int getTypicalSensorDays() {
        return typicalSensorDays;
    }

    public boolean isType2() {
        return type2;
    }

    public VersionRequest2RxMessage(byte[] packet) {
        UserError.Log.d("VR2RX", "Processing: " + JoH.bytesToHex(packet));
        if (packet.length >= 7) {
            data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            byte op = data.get();
            status = data.get();
            if (op == opcode) {
                warmupSeconds = getUnsignedShort(data);
                hardExpirationDays = getUnsignedShort(data);
                typicalSensorDays = getUnsignedShort(data);
                // crc
            } else if (op == opcode2) {
                type2 = true;
                warmupSeconds = getUnsignedShort(data);
                hardExpirationDays = getUnsignedShort(data);
                typicalSensorDays = getUnsignedShort(data);
            }
        }
    }

    public String toString() {
        return String.format(Locale.US, "Status: %s / Warmup: %d / Expiration: %d / Typical: %d",
                TransmitterStatus.getBatteryLevel(status).toString(), warmupSeconds, hardExpirationDays, typicalSensorDays);
    }

}
