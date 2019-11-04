package com.appd.enumeration;


public enum RequestTypes {
    SELECT,
    INSERT,
    UPDATE,
    DELETE;

    public static RequestTypes getRequestTypeValue(String requestType)
    {
        RequestTypes[] values = RequestTypes.values();
        for(RequestTypes value : values)
        {
            if(value.toString().equalsIgnoreCase(requestType))
                return value;
        }
        return null;
    }

}
