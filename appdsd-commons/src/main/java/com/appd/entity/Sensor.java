package com.appd.entity;

import com.appd.enumeration.SensorType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


public class Sensor {


    @JsonProperty("id")
    private Integer sensorId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("name")
    private String name;
    @JsonProperty("configured")
    private Boolean configured;
    @JsonProperty("id_location")
    private Integer locationId;

    public Sensor(Integer sensorId, SensorType sensorType, String macAddress, String serialNumber, Float hardwareVersion, Float softwareVersion) {
    }

    public Sensor(Integer sensorId, String type, String name, Boolean configured, Integer locationId) {
        this.sensorId = sensorId;
        this.type = type;
        this.name = name;
        this.configured = configured;
        this.locationId = locationId;
    }

    public Sensor() {
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId=" + sensorId +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", configure=" + configured +
                ", locationId=" + locationId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return Objects.equals(sensorId, sensor.sensorId) &&
                Objects.equals(type, sensor.type) &&
                Objects.equals(name, sensor.name) &&
                Objects.equals(configured, sensor.configured) &&
                Objects.equals(locationId, sensor.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, type, name, configured, locationId);
    }


    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
