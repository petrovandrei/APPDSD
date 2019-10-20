package com.appd.util;

import com.appd.enumeration.ConnectionStates;
import com.appd.enumeration.JSONFieldsRequest;
import com.appd.exception.BadVersionException;
import com.appd.exception.NoConnectionException;
import com.appd.socket.ClientSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

/**
 *
 * Mettre les méthodes statiques qui vont servir dans le GUI
 * Message en cas d'absence de connexion
 * Envoie d'une requête et réponse du server
 */
public class UtilGui {

    private static final Logger log = LoggerFactory.getLogger(UtilGui.class);

    //On garde la version de l'application pour les release suivantes où elle va évoluer ce qui pourrait faire des errerus
    private static final String APPLICATION_VERSION = Util.getPropertyValueFromApplicationProperties("version");
    private static String serverVersion = "";

    //message en cas d'absence de connection au server
    public static void showNoConnectionMessage() {
        JOptionPane.showMessageDialog(null, ConnectionStates.NO_CONNECTION.getFrenchLabel() + "\n La map va se fermer!", "Erreur : Le serveur est momentanément indisponible.", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    /**
     *Méthode pour envoyer une requêtre
     * @param jsonRequest : String JSON correspondant à la requête
     * @return la réponse du server
     * @throws NoConnectionException
     * @throws IOException
     * @throws BadVersionException
     */
    public static String executeRequest(String jsonRequest) throws NoConnectionException, IOException, BadVersionException
    {
        System.out.println("Rentre dans le sendRequest");
        String response = "";
        //On instancie une clientSocket
        ClientSocket clientSocket = new ClientSocket();
        //On démarre la connection
        ConnectionStates state = clientSocket.start();

        //Traitement des cas de succès ou d'ancienne version
        if(state == ConnectionStates.SUCCESS || state == ConnectionStates.DEPRECATED_VERSION)
        {
            System.out.println("CONNECTION STATE = SUCCESS");
            if(state == ConnectionStates.DEPRECATED_VERSION)
            {
                String message = ConnectionStates.DEPRECATED_VERSION.getFrenchLabel() + "\n";
                message += "Vous devez utiliser la version " + serverVersion + " de l'application car certaines fonctionnalités risquent de ne pas fonctionner.";
                JOptionPane.showMessageDialog(null, message, "Version obselète", JOptionPane.INFORMATION_MESSAGE);
            }

            //On récupère la réponse du server
            //La méthode sendRequestToServer renvoyant la réponse du server
            response = clientSocket.sendRequestToServer(jsonRequest);

            //On vérifie si l'état est RESERVED_CONNECTION pcq la réponse n'est alors pas au format JSON
            if(!jsonRequest.trim().equals(ConnectionStates.RESERVED.getCode().toString()))
            {
                String error = JsonUtil.getNodeValueOfJson(JSONFieldsRequest.ERROR_MESSAGE, response).trim();

                if(!error.equals(""))
                {
                    JOptionPane.showMessageDialog(null, error, "Erreur", JOptionPane.ERROR_MESSAGE);
                    throw new NoConnectionException();
                }


                log.debug("Réponse du serveur :\n" + response);
            }

            return response;
        }
        else
        {
            showNoConnectionMessage();
            throw new NoConnectionException();
        }
    }

    //Getter et Setter pour récupérer la version de l'application

    /**
     * @return the applicationVersion
     */
    public static String getApplicationVersion() {
        return APPLICATION_VERSION;
    }

    /**
     * @param serverVersion the serverVersion to set
     */
    public static void setServerVersion(String serverVersion) {
        UtilGui.serverVersion = serverVersion;
    }
}
