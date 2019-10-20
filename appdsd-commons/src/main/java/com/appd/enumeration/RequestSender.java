package com.appd.enumeration;

public enum RequestSender {

    CLIENT;

    public static RequestSender getClientSender(String sender) {
        RequestSender[] value = RequestSender.values();

        for(RequestSender val : value) {
            if(val.toString().equalsIgnoreCase(sender))
                return val;
        }

        return null;
    }
}
