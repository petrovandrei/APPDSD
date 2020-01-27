package com.appd.enumeration;

import org.apache.commons.lang3.math.NumberUtils;

import com.appd.util.*;

public enum SensorSensitivity {

    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH;

    public static int getNumberOfMessages(SensorSensitivity sensitivity) {
        switch (sensitivity) {
            case LOW:
                return NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("low_level"));
            case MEDIUM:
                return NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("medium_level"));
            case HIGH:
                return NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("high_level"));
            default:
                return NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("very_high_level"));
        }

    }

    public static SensorSensitivity getSensorSensitivity(String sensorSensitivity)
    {
        SensorSensitivity[] values = SensorSensitivity.values();
        for(SensorSensitivity value : values)
        {
            if(value.toString().equalsIgnoreCase(sensorSensitivity))
                return value;
        }
        return null;
    }
}