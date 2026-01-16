package com.eveningoutpost.dexdrip.profileeditor;

import com.google.gson.annotations.Expose;

import java.util.Objects;

/**
 * JamOrHam
 * represent an AAPS time/value element
 */

public class AapsElement implements Comparable<AapsElement> {

    @Expose
    String time;
    @Expose
    int timeAsSeconds;
    @Expose
    double value;

    public AapsElement() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTimeAsSeconds() {
        return timeAsSeconds;
    }

    public void setTimeAsSeconds(int timeAsSeconds) {
        this.timeAsSeconds = timeAsSeconds;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(final AapsElement o) {
        final Integer myTimeAsSeconds = timeAsSeconds;
        final Integer oTimeAsSeconds = o.timeAsSeconds;
        return myTimeAsSeconds.compareTo(oTimeAsSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AapsElement that = (AapsElement) o;
        return timeAsSeconds == that.timeAsSeconds &&
                Double.compare(that.value, value) == 0 &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, timeAsSeconds, value);
    }

    @Override
    public String toString() {
        return "AapsElement{" +
                "time='" + time + '\'' +
                ", timeAsSeconds=" + timeAsSeconds +
                ", value=" + value +
                '}';
    }
}
