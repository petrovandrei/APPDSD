package com.appd.exception;

public class BadVersionException extends Exception{

    private static final long serialVersionUID = 1L;

    /**
     * @param minimumVersion - the version of the server
     */
    public BadVersionException(String minimumVersion)
    {
        super("La version de l'application est incompatible. Il faut au minimum la version : " + minimumVersion + " ! Le server va être accessible mais certaines fonctionnalités risquent de ne pas fonctionner.");
    }
}
