package com.appd.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;

import com.appd.connection.pool.implementation.DataSource;
import com.appd.dao.entityDao.DAOFactory;
import com.appd.enumeration.ConnectionStates;
import com.appd.enumeration.JSONFieldsRequest;
import com.appd.enumeration.RequestSender;
import com.appd.enumeration.RequestTypes;
import com.appd.util.JsonUtil;
import com.appd.util.Util;

import com.appd.util.UtilServer;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *Avoir une connection,
 * lire les échanges entre client et server
 * Executer la requête du client
 * Rendre la connexcion
 */
public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    //Pour lire le message du client
    private BufferedReader readFromClient;

    //Pour renvoyer une réponse au client
    private PrintWriter writeToClient;

    private Socket socket;
    private Connection connection;
    //ObjectMapper pour le client
    private ObjectMapper mapper;

    //Le temps pour gérer les délais, TIMEOUT
    private final int oneSecondMs = DateTimeConstants.MILLIS_PER_SECOND;
    //Encodage lecture ud fichier properties
    private final String encodageType = Util.getPropertyValueFromApplicationProperties("encodage_type");

    //Constructor
    public RequestHandler(Socket socket, Connection connection) {
        this.socket = socket;
        this.connection = connection;
        mapper = new ObjectMapper();
    }

    @Override
    public void run() {

        try
        {
            readFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), encodageType));
            writeToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encodageType), true);

            String[] ipAddressAndVersion = readFromClient.readLine().split(Util.getVersionSplitter());



            //On regarde la version de l'app client
            //Puis on vérifie qu'il s'agit des mêmes versions
            //Pas ce problème pour la R0
            String clientVersion = ipAddressAndVersion[1];
            String serverVersion = UtilServer.getApplicationVersion();
            if(!clientVersion.equalsIgnoreCase(serverVersion))
            {
                writeToClient.println(ConnectionStates.DEPRECATED_VERSION.getCode() + "v." + serverVersion);
            }
            else
            {
                writeToClient.println("");
            }

            //Cas où c'est la bonne version, la connexion est réservée
            String requestOfClient = readFromClient.readLine();
            String reservedConnectionCode = ConnectionStates.RESERVED.getCode().toString();
            String ipAddress = ipAddressAndVersion[0];

            //On regarde si réservaion requête par client
            if(requestOfClient.trim().equalsIgnoreCase(reservedConnectionCode))
            {
                int reservedTimeInMilliseconds = NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("reserved_time_ms"));
                String reservedTime = (new Integer(reservedTimeInMilliseconds / oneSecondMs)).toString();
                log.info("Un client a réservé une connexion pour " + reservedTime + " sec\n");
                String message = reservedTime + " sec-" + reservedTime;
                writeToClient.println(message);
                //On sleep alors le thread pour que la connexion ne soit pas accessible par personne d'autre
                Thread.sleep(reservedTimeInMilliseconds);
                log.info("Cette connexion est déjà réservée..");
            }
            else
            {
                JsonNode json = mapper.readTree(requestOfClient);
                RequestSender requestSender = RequestSender.getClientSender(JsonUtil.getNodeValueOfJson(JSONFieldsRequest.REQUEST_SENDER, requestOfClient));

                if(requestSender == RequestSender.CLIENT) {
                    log.info("Requête reçue du client "+ipAddress+" :\n" + requestOfClient + "\n");
                    String responseToClient = executeClientRequest(json);
                    log.info("Réponse du serveur au client "+ipAddress+" :\n" + responseToClient + "\n");
                    writeToClient.println(responseToClient);
                }


            }

        }
        catch (Exception e)
        {
            log.error("Une erreur a eu lieu : le client est déconnecté..");
        }
        finally
        {
            //On cloture la connexion
            exit();
        }
    }


    @SuppressWarnings("finally")
    /*
    Pour exécuter la requête reçue par le client
     */
    public String executeClientRequest(JsonNode json)
    {
        //Result instancié à empty
        String result = "";

        try
        {
            // JSON Node qui contient les infos de la requête
            JsonNode requestNode = json.get(JSONFieldsRequest.REQUEST_INFO.getLabel());
            String requestEntity = requestNode.get(JSONFieldsRequest.REQUESTED_LABEL_CLASS.getLabel()).textValue();
            Class<?> entityClass = Class.forName(requestEntity);

            //Les champs sur lesquels on filtre
            String fieldsStringFromJson = requestNode.get(JSONFieldsRequest.REQUESTED_FIELDS.getLabel()).toString();
            //Les valeurs des filtres
            String valuesStringFromJson = requestNode.get(JSONFieldsRequest.REQUIRED_VALUES.getLabel()).toString();
            //Les tests
            String testsStringFromJson = requestNode.get(JSONFieldsRequest.REQUIRED_TESTS.getLabel()).toString();

            List<String> fields = null;
            List<String> requiredValues = null;
            List<String> tests = null;

            if(fieldsStringFromJson != null && valuesStringFromJson != null)
            {
                fields = mapper.readValue(fieldsStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                requiredValues = mapper.readValue(valuesStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                tests = mapper.readValue(testsStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            }

            Object deserializedObject = getObjectFromJson(json);

            RequestTypes requestTypes = RequestTypes.getRequestTypeValue(requestNode.get(JSONFieldsRequest.REQUEST_TYPE_CRUD.getLabel()).textValue());

            //dans service
            //TODO : ajouter la classe
            Object objectResult = DAOFactory.execute(connection, entityClass, requestTypes, deserializedObject, fields, requiredValues, tests);

            result = JsonUtil.serializeObjectToJSON(objectResult, entityClass, "");



        }
        catch (Exception e)
        {
            log.error("Une erreur est survenue lors de l'exécution de la requête du client :\n" + e.getMessage());
        }
        finally
        {
            //On retourne le résultat de la requête
            return result;
        }

    }

    /**
     * Transformation du JSON en objet
     * @param json
     * @return Object
     */
    private Object getObjectFromJson(JsonNode json)
    {
        JsonNode serializedObjectNode = json.get(JSONFieldsRequest.SERIALIZED_OBJECT.getLabel());
        if(!serializedObjectNode.isNull())
            return JsonUtil.deserializeJsonObjectToJavaObjet(serializedObjectNode.toString());
        return null;
    }

    /**
     * Méthode pour fermer une connexion et la remettre dans le pool de connexions
     */
    private void exit()
    {
        try
        {
            //On rend la connexion au pool
            DataSource.putConnection(connection);
            //on met la connexion à null
            this.connection = null;
            //on le ferme le reste
            socket.close();
            readFromClient.close();
            writeToClient.close();
        }
        catch (IOException e)
        {
            log.error("Cette erreur est survenue lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
