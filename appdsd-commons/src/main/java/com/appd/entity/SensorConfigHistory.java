package com.appd.entity;

import com.appd.enumeration.SensorAction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class SensorConfigHistory {

    @JsonProperty("history_id")
    private int idHistory;
    @JsonProperty("sensor_id")
    private int idSensorSource;
    @JsonProperty("measured_threshold")
    private Float measuredThreshold;
    @JsonProperty("min_danger_threshold")
    private Float minDangerThreshlod;
    @JsonProperty("max_danger_threshold")
    private Float maxDangerThreshlod;
    @JsonProperty("measurement_date")
    private Timestamp date;
    @JsonProperty("end_alert_date")
    private Timestamp endAlertDate;
    @JsonProperty("description")
    private String description;
    @JsonProperty("action_done")
    private SensorAction actionDone;

    public SensorConfigHistory() {
    }

    public SensorConfigHistory(int idHistory, int idSensorSource, Float measuredThreshold,  Float minDangerThreshlod,
                                      Float maxDangerThreshlod,
                                      Timestamp date, Timestamp endAlertDate, String description, SensorAction actionDone) {
        this.idHistory = idHistory;
        this.idSensorSource = idSensorSource;
        this.measuredThreshold = measuredThreshold;
        this.minDangerThreshlod = minDangerThreshlod;
        this.maxDangerThreshlod = maxDangerThreshlod;
        this.date = date;
        this.endAlertDate = endAlertDate;
        this.description = description;
        this.actionDone = actionDone;
    }

    // Constructor used to register in the database
    public SensorConfigHistory(SensorConfiguration sensor, float threasholdReach, Timestamp dangerTime, String description, SensorAction actionDone) {
        this(0, sensor.getSensorConfigurationId(), threasholdReach,
                sensor.getMinDangerThreshold(), sensor.getMaxDangerThreshold(),
                dangerTime, null, description, actionDone);
    }

    public int getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(int idHistory) {
        this.idHistory = idHistory;
    }

    public int getIdSensorSource() {
        return idSensorSource;
    }

    public void setIdSensorSource(int idSensorSource) {
        this.idSensorSource = idSensorSource;
    }

    public Float getMeasuredThreshold() {
        return measuredThreshold;
    }

    public void setMeasuredThreshold(Float measuredThreshold) {
        this.measuredThreshold = measuredThreshold;
    }

    public Float getMinDangerThreshlod() {
        return minDangerThreshlod;
    }

    public void setMinDangerThreshlod(Float minDangerThreshlod) {
        this.minDangerThreshlod = minDangerThreshlod;
    }

    public Float getMaxDangerThreshlod() {
        return maxDangerThreshlod;
    }

    public void setMaxDangerThreshlod(Float maxDangerThreshlod) {
        this.maxDangerThreshlod = maxDangerThreshlod;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SensorAction getActionDone() {
        return actionDone;
    }

    public void setActionDone(SensorAction actionDone) {
        this.actionDone = actionDone;
    }

    public Timestamp getEndAlertDate() {
        return endAlertDate;
    }

    public void setEndAlertDate(Timestamp endAlertDate) {
        this.endAlertDate = endAlertDate;
    }
}
