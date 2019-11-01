package com.appd.enumeration;


public enum ConnectionStates {


    DEPRECATED_VERSION(5,"The version of the application you are using is deprecated", "Vous utilisez une version obselète de l'application"),

    NO_CONNECTION(1, "There is no connection left ! Please try later.", "Absence de connexion disponible ! Veuillez réessayer plus tard."),

    RESERVED(4, "The connection has been reserved", "La connexion a été réservée"),

    SUCCESS(3,"The connection succeeded", "Connexion au serveur réussie");


    private Integer code;
    private String englishLabel;
    private String frenchLabel;

    ConnectionStates(Integer code, String englishLabel, String frenchLabel) {
        this.code=code;
        this.englishLabel=englishLabel;
        this.frenchLabel=frenchLabel;
    }

    public Integer getCode() {
        return code;
    }


    public String getEnglishLabel() {
        return englishLabel;
    }

    public String getFrenchLabel() {
        return frenchLabel;
    }

}
