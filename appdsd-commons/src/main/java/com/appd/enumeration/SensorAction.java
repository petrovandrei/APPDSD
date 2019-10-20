package com.appd.enumeration;

public enum SensorAction {

    CONFIGURATION,

    STOP_DANGER_ALERT,

    CLOSE_DOOR,

    CLOSE_WINDOW,

    UNLOCK_CODE,

    SWITCH_OFF_LIGHT,

    FAKE_ALERT;

    public static SensorAction getValueOf(String str) {
        SensorAction[] values = SensorAction.values();
        for(SensorAction action : values) {
            if(action.name().equalsIgnoreCase(str))
                return action;
        }
        return null;
    }

}
