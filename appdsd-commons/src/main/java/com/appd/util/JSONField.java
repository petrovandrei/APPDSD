package com.appd.util;

public enum JSONField {

    ENTITY("entity"),

    DATAS("datas"),

    IS_LIST("is_list_of_entities"),

    REQUEST_INFO("request_info"),

    REQUEST_TYPE("request_type"),

    REQUESTED_FIELDS("requested_fields"),

    REQUIRED_VALUES("required_values"),

    REQUIRED_TESTS("required_tests"),

    REQUESTED_ENTITY("requested_entity"),

    SERIALIZED_OBJECT("serialized_object"),

    REQUEST_SENDER("request_sender"),

    CACHE_SENSOR_STATE("cache_sensor_state"),

    CACHE_SENSOR_MAP("cache_sensor_list_map"),

    //The message fields will allow us to know if the serialization or deserialization happened without failures
    ERROR_MESSAGE("error_message");


    private String label;

    JSONField(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }

}

