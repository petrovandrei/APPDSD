package com.appd.enumeration;

public enum SensorActivity {

    ENABLED,

    DISABLED,

    NOT_CONFIGURED;


    public static SensorActivity getSensorActivity(String sensorActivity)
    {
        SensorActivity[] values = SensorActivity.values();
        for(SensorActivity value : values)
        {
            if(value.toString().equalsIgnoreCase(sensorActivity))
                return value;
        }
        return null;
    }
}
