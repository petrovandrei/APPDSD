package com.appd.enumeration;

public enum SensorType {

    SMOKE("Smoke", 2, 3, false, false),
    FLOW("Flow", 5, 7, false, true, "Empty", "Not Empty"),
    DOOR("Door", 11, 13, false, true, "Closed", "Open"),
    TEMPERATURE("Temperature", 17, 19, true, false),
    WINDOW("Window", 23, 29, false, true),
    ACCESS_CONTROL("Access control", 83, 89, false, true, "GRANTED", "NOT_GRANTED");


    private String label;
    private int defaultCode;
    private int dangerCode;
    private boolean isGapAcceptable;
    private boolean isBinary;
    private String defaultMessage;
    private String dangerMessage;

    /**
     * @param label
     * @param defaultCode
     * @param dangerCode
     * @param isGapAcceptable
     * @param isBinary
     */

    SensorType(String label, int defaultCode, int dangerCode, boolean isGapAcceptable, boolean isBinary) {
        this.label = label;
        this.defaultCode = defaultCode;
        this.dangerCode = dangerCode;
        this.isBinary = isBinary;
        this.isGapAcceptable = isGapAcceptable;
    }

    SensorType(String label, int defaultCode, int dangerCode, boolean isGapAcceptable, boolean isBinary, String defaultMessage, String dangerMessage) {
        this(label, defaultCode, dangerCode, isGapAcceptable, isBinary);
        this.defaultMessage = defaultMessage;
        this.isGapAcceptable = isGapAcceptable;
    }

    public static SensorType getSensorType(String sensorType) {
        SensorType[] values = SensorType.values();
        for (SensorType value : values) {
            if (value.toString().equalsIgnoreCase(sensorType))
                return value;
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public int getDefaultCode() {
        return defaultCode;
    }

    public int getDangerCode() {
        return dangerCode;
    }

    public boolean isGapAcceptable() {
        return isGapAcceptable;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public static SensorAction getActionAssociatedToStopDanger(SensorType type) {
        switch (type) {
            case DOOR:
                return SensorAction.CLOSE_DOOR;
            case WINDOW:
                return SensorAction.CLOSE_WINDOW;
            case ACCESS_CONTROL:
                return SensorAction.UNLOCK_CODE;
            default:
                return SensorAction.STOP_DANGER_ALERT;
        }
    }

    public String getMessageAccordingToState(SensorState state) {
        if (state == SensorState.DANGER || (state == SensorState.WARNING && this == SensorType.ACCESS_CONTROL))
            return this.dangerMessage;
        return this.defaultMessage;
    }
}
