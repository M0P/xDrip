package com.eveningoutpost.dexdrip.cgm.dex;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * JamOrHam
 */

public class ClassifierSignpost {
    final public UUID uuid;
    final public String action;
    public BluetoothGattCharacteristic characteristic;

    public ClassifierSignpost(UUID uuid, String action) {
        this.uuid = uuid;
        this.action = action;
    }
}
