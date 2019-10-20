package com.appd.exception;

public class UnknownClassException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownClassException(Class<?> entityClass) {
        super("La classe '" + entityClass.getSimpleName() + "' est inconnue et n'existe pas dans ce projet !");
    }
}
