

package com.appd.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.appd.enumeration.SensorState;
import com.appd.enumeration.SensorType;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Sensor {

    @JsonProperty("sensor_id")
    protected Integer sensorId;
    @JsonProperty("sensor_type")
    protected SensorType sensorType;
    @JsonProperty("mac_address")
    protected String macAddress;
    @JsonProperty("serial_number")
    protected String serialNumber;
    @JsonProperty("hardware_version")
    protected Float hardwareVersion;
    @JsonProperty("software_version")
    protected Float softwareVersion;
    @JsonIgnore
    private SensorState state;

//	public Sensor(String sensorMark, SensorType sensorType, double sensorPrice, int sensorInterviewPrice)
//	{
//		super();
//		this.sensorMark = sensorMark;
//		this.sensorType = sensorType;
//		this.sensorPrice = sensorPrice;
//		this.sensorInterviewPrice = sensorInterviewPrice;
//	}

    public Sensor(Integer sensorId, SensorType sensorType, String macAddress, String serialNumber, Float hardwareVersion,
                  Float softwareVersion) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
        this.hardwareVersion = hardwareVersion;
        this.softwareVersion = softwareVersion;
    }

    public Sensor() {}

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Float getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(Float hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public Float getSoftwareVersion() {
        return softwareVersion;
    }


    public void setSoftwareVersion(Float softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sensorId == null) ? 0 : sensorId.hashCode());
        result = prime * result + ((sensorType == null) ? 0 : sensorType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sensor other = (Sensor) obj;
        if (sensorId == null) {
            if (other.sensorId != null)
                return false;
        } else if (!sensorId.equals(other.sensorId))
            return false;
        if (sensorType != other.sensorType)
            return false;
        return true;
    }

    public void setState(SensorState state) {
        this.state = state;
    }

    public SensorState getState() {
        if(state == null)
            return SensorState.DEFAULT;
        return state;
    }



}
