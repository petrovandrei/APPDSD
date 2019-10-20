package com.appd.exception;

import com.appd.enumeration.ConnectionStates;


public class NoConnectionException extends Exception{

    public NoConnectionException() {
        super(ConnectionStates.NO_CONNECTION.getFrenchLabel());
    }
}
