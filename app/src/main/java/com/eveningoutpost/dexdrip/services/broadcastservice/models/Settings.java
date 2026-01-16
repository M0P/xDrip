package com.eveningoutpost.dexdrip.services.broadcastservice.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Settings implements Parcelable {
    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    /**
     * Defines graph start offset in ms, if not defined, the offset would be Constants.HOUR_IN_MS *2.
     * Would be used only if enabled displayGraph
     */
    private long graphStart;

    /**
     * Defines graph end offset in ms, if not defined, the offset would be current time.
     * Would be used only if enabled displayGraph
     */
    private long graphEnd;

    /**
     * Recipient application name
     */
    private String apkName;

    /**
     * If enabled, will send a graph lines data to recipient
     */
    private boolean displayGraph;

    public Settings(Parcel in) {
        apkName = in.readString();
        graphStart = in.readLong();
        graphEnd = in.readLong();
        displayGraph = in.readInt() == 1;
    }

    public Settings() {

    }

    public long getGraphStart() {
        return graphStart;
    }

    public void setGraphStart(long graphStart) {
        this.graphStart = graphStart;
    }

    public long getGraphEnd() {
        return graphEnd;
    }

    public void setGraphEnd(long graphEnd) {
        this.graphEnd = graphEnd;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public boolean isDisplayGraph() {
        return displayGraph;
    }

    public void setDisplayGraph(boolean displayGraph) {
        this.displayGraph = displayGraph;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(apkName);
        parcel.writeLong(graphStart);
        parcel.writeLong(graphEnd);
        parcel.writeInt(displayGraph ? 1 : 0);
    }
}
