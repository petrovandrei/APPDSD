package com.appd.alert;

import com.appd.enumeration.SensorState;
import com.appd.util.Util;

import java.awt.image.TileObserver;
import java.sql.Timestamp;

public class Cache {

    private int warningCount;
    private Timestamp lastMessageDate;
    private Timestamp firstDangerMessageDate;
    private SensorState sensorState;
    private int history;
    private boolean inDanger;
    private float thresholdReached;

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public Timestamp getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Timestamp lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Timestamp getFirstDangerMessageDate() {
        return firstDangerMessageDate;
    }

    public void setFirstDangerMessageDate(Timestamp firstDangerMessageDate) {
        this.firstDangerMessageDate = firstDangerMessageDate;
    }

    public SensorState getSensorState() {
        return sensorState;
    }

    public void setSensorState(SensorState sensorState) {
        this.sensorState = sensorState;
        if(sensorState == SensorState.DEFAULT)
            this.warningCount = 0;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public boolean isInDanger() {
        return inDanger;
    }

    public void setInDanger(boolean inDanger) {
        this.inDanger = inDanger;
    }

    public float getThresholdReached() {
        return thresholdReached;
    }

    public void setThresholdReached(float thresholdReached) {
        this.thresholdReached = thresholdReached;
    }

    public Cache(Timestamp lastMessageDate, Timestamp firstDangerMessageDate, SensorState sensorState, float thresholdReached){
        this.warningCount = (sensorState == SensorState.WARNING) ? 1 : 0;
        this.lastMessageDate = lastMessageDate;
        this.firstDangerMessageDate = firstDangerMessageDate;
        this.sensorState = sensorState;
        this.history = 0;
        inDanger = false;
        this.thresholdReached = thresholdReached;
    }

    public SensorState addWarning(int maxWarningCount, Timestamp messageDate) {
        this.warningCount++;

        if(this.warningCount >= maxWarningCount) {
            sensorState = SensorState.DANGER;
            if(warningCount == maxWarningCount && messageDate != null) {
                firstDangerMessageDate = messageDate;
            }
        }
        else
            sensorState = SensorState.WARNING;

        return sensorState;
    }

    public SensorState addWarning(int maxWarningCount) {
        return addWarning(maxWarningCount, null);
    }

    public void reset() {
        this.warningCount = 0;
        firstDangerMessageDate = null;
        sensorState = SensorState.DEFAULT;
        history = 0;
        inDanger = false;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "warningCount=" + warningCount +
                ", lastMessageDate=" + lastMessageDate +
                ", firstDangerMessageDate=" + firstDangerMessageDate +
                ", sensorState=" + sensorState +
                ", history=" + history +
                ", inDanger=" + inDanger +
                ", thresholdReached=" + thresholdReached +
                '}';
    }
}

