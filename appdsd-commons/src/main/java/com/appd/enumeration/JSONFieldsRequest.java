package com.appd.enumeration;

/*
Enumeration pour construction des flux JSON
 */
public enum JSONFieldsRequest {


    ENTITY("entity"),

    DATA("datas"),

    IS_LIST_OF_SENSORS("is_list_of_entities"),

    REQUEST_INFO("request_info"),


    REQUEST_TYPE_CRUD("request_type"),

    REQUESTED_LABEL_CLASS("requested_entity"),

    REQUESTED_FIELDS("requested_fields"),
    REQUIRED_VALUES("required_values"),
    REQUIRED_TESTS("required_tests"),

    SERIALIZED_OBJECT("serialized_object"),

    REQUEST_SENDER("request_sender"),

    ERROR_MESSAGE("error_message");

    private String label;

    JSONFieldsRequest(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return this.label;
    }
}
