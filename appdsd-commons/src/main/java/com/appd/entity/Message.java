package com.appd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Message {

    @JsonProperty("sensor_id")
    private Integer sensorId;
    @JsonProperty("threshold_reached")
    private Float thresholdReached;
    @JsonProperty("message_creation_date")
    private Timestamp creationDate;

    public Message(Integer sensorId, Float thresholdReached) {
        this.sensorId = sensorId;
        this.thresholdReached = thresholdReached;
        this.creationDate = new Timestamp(System.currentTimeMillis());
    }

    public Message() { }

    public Integer getSensorId() {
        return sensorId;
    }

    public Float getThresholdReached() {
        return thresholdReached;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sensorId=" + sensorId +
                ", thresholdReached=" + thresholdReached +
                ", creationDate=" + creationDate +
                '}';
    }
}
