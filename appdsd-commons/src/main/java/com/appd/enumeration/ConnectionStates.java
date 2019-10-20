package com.appd.enumeration;

/*
Enumeration montrant les différents états que peut prendre la connection
 */
public enum ConnectionStates {

    //Lorsqu'on appelle le server on peut appeler une mauvaise version, une erreur sera alors soulevée
    DEPRECATED_VERSION(5,"The version of the application you are using is deprecated", "Vous utilisez une version obselète de l'application"),

    //Utilisée pour lever une exception lorsqu'il n'y a pas de connection
    //Egalement état initial d'une connection
    NO_CONNECTION(1, "There is no connection left ! Please try later.", "Absence de connexion disponible ! Veuillez réessayer plus tard."),

    //Lors de l'envoie d'une requête à un server, on prévient que la connection est réservée
    RESERVED(4, "The connection has been reserved", "La connexion a été réservée"),

    //La connection est accordée on est pas dans une mauvaise version, aucune excpetion n'a été soulevée
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

    /**
     * @return the englishLabel
     */
    public String getEnglishLabel() {
        return englishLabel;
    }

    /**
     * @return the frenchLabel
     */
    public String getFrenchLabel() {
        return frenchLabel;
    }

}
