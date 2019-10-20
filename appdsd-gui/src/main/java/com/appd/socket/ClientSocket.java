package com.appd.socket;

import com.appd.enumeration.ConnectionStates;
import com.appd.exception.BadVersionException;
import com.appd.util.Util;
import com.appd.util.UtilGui;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
/**
 * Classe ClientSocket :
 * Obtenir une connexion au server
 * Envoyer une requête au server et recevoir sa réponse
 * Fermer la connexion au server
 */
public class ClientSocket {

    //variables
    private static final Logger log = LoggerFactory.getLogger(ClientSocket.class);
    private final String SERVER_IP = Util.getPropertyValueFromApplicationProperties("server_ip");
    private final int PORT = Integer.parseInt(Util.getPropertyValueFromApplicationProperties("server_port"));
    //lecture depuis le server
    private BufferedReader readFromServer;
    //réponse au server
    private PrintWriter writeToServer;

    /**
     *TIMEOUT : délai maximal de réponse du serveur, au delà de ce délai, on considère que le server est indisponible
     */
    private final int TIMEOUT = NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("server_response_limit"));

    private Socket socket;
    private InetAddress ipAddress;
    private final String versionSplitter = Util.getVersionSplitter();
    private final String encodageType = Util.getPropertyValueFromApplicationProperties("encodage_type");

    //Conctructor empty
    public ClientSocket() {

    }

    @SuppressWarnings("finally")
    //Méthode pour démarrer une connection
    public ConnectionStates start()
    {
        //Etat initial de la connection : pas de connection
        ConnectionStates connectionState = ConnectionStates.NO_CONNECTION;

        System.out.println("ETAT CONNECTION = NO");

        try
        {
            System.out.println("Rentre dans le try");
            System.out.println(SERVER_IP + " " + PORT);
            // Connection à une socket
            socket = new Socket(SERVER_IP, PORT);
            System.out.println(SERVER_IP + " " + PORT);
            System.out.println("NEW SOCKET");

            /*
             * On soulève une execption "SocketTimeoutException" : lorsque que le délai TIMEOUT est atteint
             */
            socket.setSoTimeout(TIMEOUT);

            readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), encodageType));
            writeToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encodageType), true);
            ipAddress = InetAddress.getLocalHost();
            //Envoie le split. Utile en cas de pb de version //Pas sûre que ça me serve au final pour l'instant
            writeToServer.println(ipAddress + versionSplitter + UtilGui.getApplicationVersion());

            //On vérifie donc qu'on appelle la bonne version
            //R0 qu'une seule version
            String[] serverCheck = readFromServer.readLine().split(versionSplitter);
            if(serverCheck.length >= 2)
            {
                String code = serverCheck[0];
                String serverVersion = serverCheck[1];
                //On soulève une exception si on appelle une ancienne version du server ou mauvaise version
                //Pas le cas en R0
                if(code.equalsIgnoreCase(ConnectionStates.DEPRECATED_VERSION.getCode().toString()))
                {
                    UtilGui.setServerVersion(serverVersion);
                    throw new BadVersionException(serverVersion);
                }

            }
            //Pas de mauvaise version, la connection prend donc l'état de succès
            connectionState = ConnectionStates.SUCCESS;
        }
        catch(BadVersionException e)
        {
            log.error(e.getMessage());
            //L'exeption deprecatedVersion a été soulevée on passe donc l'état associé à l'état de la connection
            connectionState = ConnectionStates.DEPRECATED_VERSION;
        }
        catch (SocketTimeoutException e)
        {
            //L'erreur TimeOut a été soulevé
            //On n'associe pas d'état particulier mais on le met dans les logs pour avertir
            log.error("Le délai pour avoir une connection a été atteint : " + e.getMessage() + ".\n Le server ne peut donc répondre à votre dernière requête.");
            exit();
        }
        catch (Exception e)
        {
            log.error("Deconnection du serveur : " + e.getMessage());
            exit();
        }
        finally
        {
            return connectionState;
        }
    }

    /**
     *
     * @param requestToSendToServer : La requête à envoyer au server
     * @return La réponse du server
     * @throws IOException
     * Méthode donc pour envoyer une requête au server et recevoir sa réponse
     */
    public String sendRequestToServer(String requestToSendToServer) throws IOException
    {
        //Réponse initialisée à vide
        String responseFromServer = "";

        //Cas où l'accès à une connection est réservée
        if(requestToSendToServer.trim().equals(ConnectionStates.RESERVED.getCode().toString()))
            log.info("Vous réservez une connexion");

        // On envoie la requête au server
        writeToServer.println(requestToSendToServer);

        //On récupère la réponse du server
        responseFromServer = readFromServer.readLine();

        //On ferme la communication au server
        exit();
        return responseFromServer;
    }

    //Méthode pour fermer la communication au server
    private void exit()
    {
        try {
            if(readFromServer != null)
                readFromServer.close();
            if(writeToServer != null)
                writeToServer.close();
            if(socket != null)
            {
                socket.close();
                log.debug("La communication avec le server a été coupée avec succès. ");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
