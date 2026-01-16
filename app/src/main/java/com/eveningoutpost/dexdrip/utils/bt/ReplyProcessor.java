package com.eveningoutpost.dexdrip.utils.bt;

import com.polidea.rxandroidble2.RxBleConnection;

// jamorham

public abstract class ReplyProcessor {

    private Object tag;

    protected byte[] mOutbound;
    private final RxBleConnection connection;

    public ReplyProcessor(RxBleConnection connection) {
        this.connection = connection;
    }

    public abstract void process(byte[] bytes);

    public Object getTag() {
        return tag;
    }

    public RxBleConnection getConnection() {
        return connection;
    }

    public ReplyProcessor setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public ReplyProcessor setOutbound(final byte[] tag) {
        this.mOutbound = tag;
        return this;
    }

}
