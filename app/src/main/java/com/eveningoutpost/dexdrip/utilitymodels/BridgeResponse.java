package com.eveningoutpost.dexdrip.utilitymodels;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by jamorham on 16/03/2018.
 */

public class BridgeResponse {

    private final LinkedList<ByteBuffer> send;
    private String error_message;
    private long delay;
    private boolean still_waiting_for_data = false;
    private boolean got_all_data = false;

    public BridgeResponse() {
        send = new LinkedList<>();
    }

    public LinkedList<ByteBuffer> getSend() {
        return send;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isStill_waiting_for_data() {
        return still_waiting_for_data;
    }

    public void setStill_waiting_for_data(boolean still_waiting_for_data) {
        this.still_waiting_for_data = still_waiting_for_data;
    }

    public boolean isGot_all_data() {
        return got_all_data;
    }

    public void setGot_all_data(boolean got_all_data) {
        this.got_all_data = got_all_data;
    }


    public boolean hasError() {
        return error_message != null;
    }

    public void add(ByteBuffer buffer) {
        send.add(buffer);
    }

    public boolean shouldDelay() {
        return delay > 0;
    }

    public boolean StillWaitingForData() {
        return still_waiting_for_data;
    }

    public void SetStillWaitingForData() {
        still_waiting_for_data = true;
    }

    public boolean GotAllData() {
        return got_all_data;
    }

    public void SetGotAllData() {
        got_all_data = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BridgeResponse that = (BridgeResponse) o;
        return delay == that.delay &&
                still_waiting_for_data == that.still_waiting_for_data &&
                got_all_data == that.got_all_data &&
                Objects.equals(send, that.send) &&
                Objects.equals(error_message, that.error_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(send, error_message, delay, still_waiting_for_data, got_all_data);
    }

    @Override
    public String toString() {
        return "BridgeResponse{" +
                "send=" + send +
                ", error_message='" + error_message + '\'' +
                ", delay=" + delay +
                ", still_waiting_for_data=" + still_waiting_for_data +
                ", got_all_data=" + got_all_data +
                '}';
    }
}
