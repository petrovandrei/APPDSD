package com.appd.util;

import com.appd.enumeration.JSONFieldsRequest;
import com.appd.enumeration.RequestSender;
import com.appd.enumeration.RequestTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.util.List;


public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * Convertir un objet JAVA en object JSON au format texte (String)
     *
     * @param object :
     * 			L'objet à convertir en JSON, une simple entité ou une liste d'entités
     * @param objectClass :
     * 			La classe de l'objet à sérialiser. Important de l'avoir dans le cas où l'objet est à null.
     * @return
     * 			La chaîne de caractère en JSON qui représente l'objet
     * 		//TODO : rechercher sur cette exception
     * @throws InvalidAttributeValueException
     */
    @SuppressWarnings("rawtypes")
    public static String serializeObjectToJSON(Object object, Class objectClass, String message)
    {
        String objectToJSON = null;

        //pour JSSON
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        message = (message == null) ? " " : message;
        objectNode.put(JSONFieldsRequest.ERROR_MESSAGE.getLabel(), message);

        try {

            if(object != null && objectClass != null)
            {
                /*
                 * L'objet peut être une liste si on utilise findAllSensors
                 * instanceof permet de connaître le type
                 */
                if(object instanceof List)
                {
                    //on passe à true
                    objectNode.put(JSONFieldsRequest.IS_LIST_OF_SENSORS.getLabel(), true);
                }
                else
                {
                    //on passe à false
                    objectNode.put(JSONFieldsRequest.IS_LIST_OF_SENSORS.getLabel(), false);
                }

                String className = objectClass.getName();
                /*
                 * On stock le nom de l'entité pour rendre la désérialisation plus simple ensuite
                 *Il n'y a q'une seule méthode de désérialisation pour toutes les entités
                 */
                objectNode.put(JSONFieldsRequest.ENTITY.getLabel(), className);
                objectNode.putPOJO(JSONFieldsRequest.DATA.getLabel(), object);
            }
            else
            {
                log.error("Sérialisation impossible : l'objet est à null.");
            }

            objectToJSON = objectMapper.writeValueAsString(objectNode);
        }
        catch (Exception e)
        {
            log.error("La sérialisation en JSON a échoué: " + e.getMessage());
        }

        return objectToJSON;
    }

    /**
     * Méthode pour convertir du texte JSON en objet JAVA
     * @param objectInJSONString :
     * 			La chaîne JSON qui correspond à notre objet
     * @return
     * 			L'objet java qui est converti depuis la chaîne JSON
     */
    public static Object deserializeJsonObjectToJavaObjet(String objectInJSONString) {

        Object resultObject = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            if(objectInJSONString == null || objectInJSONString.trim().length() == 0)
                throw new Exception("La chaîne à désérialiser est à null. ");

            // Du String au JSON Node
            //TODO : readTree ?
            JsonNode objectJStringNode = objectMapper.readTree(objectInJSONString);
            // On lui passe le nom de l'entité
            JsonNode entityNode = objectJStringNode.get(JSONFieldsRequest.ENTITY.getLabel());
            //On lui passe les données (l'objet)
            JsonNode datasNode = objectJStringNode.get(JSONFieldsRequest.DATA.getLabel());
            // On vérifie s'il s'agit d'une liste uo non
            JsonNode SensorsList = objectJStringNode.get(JSONFieldsRequest.IS_LIST_OF_SENSORS.getLabel());

            // On récupère le nom de la classe
            String className = entityNode.textValue();

            //On récupère la classe de l'entité une fois qu'on a son nom
            Class<?> objectClass = Class.forName(className);

            //On stock le boolean de sorte à savoir s'il s'agit d'une liste ou non
            boolean isListOfSensors = SensorsList.booleanValue();

            if(isListOfSensors)
                //si c'est une liste on construit la liste
                resultObject = objectMapper.readValue(datasNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, objectClass));
            else
                resultObject = objectMapper.readValue(datasNode.toString(), objectClass);


        } catch (Exception e) {
            log.error("La désérialisation a échouée car il n'y avait pas d'objet à désérialiser  (SELECT clause) : " + e.getMessage());
        }

        return resultObject;
    }




    @SuppressWarnings("rawtypes")
    /**
     *  Méthode pour construire la requête de sérialisation
     */
    public static String serializeRequestConstruct(RequestTypes requestTypes, Class entityClass, String serializedObject,
                                                   List<String> requestedFields, List<String> requiredValues, List<String> requiredTests,
                                                   RequestSender requestSender)
    {
        String javaobjectToJSONobj = null;
        //TODO :
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        try
        {

                if(requestTypes == null || entityClass == null)
                    throw new IOException("Le type de requête et/ou l'entité ne peuvent être à null.");

                javaobjectToJSONobj = null;

                ObjectNode requestNode = objectMapper.createObjectNode();

                if(serializedObject == null)
                    serializedObject = "";
                JsonNode serializedObjectNode = objectMapper.readTree(serializedObject);

                requestNode.put(JSONFieldsRequest.REQUEST_TYPE_CRUD.getLabel(), requestTypes.toString());
                requestNode.put(JSONFieldsRequest.REQUESTED_LABEL_CLASS.getLabel(), entityClass.getName());

                if(requestedFields != null && requiredValues != null)
                {
                    if((requestedFields.size() != requiredValues.size()))
                    {
                        requestedFields = null;
                        requiredValues = null;
                        log.error("Une erreur est survenue durant la requête de sérialisation : le nombre de champs et le nombre de valeurs requises sont différents, ils vont alors être mis à null.");
                    }
                }


                /**
                 * Ces listes vont être ajoutées à la chaîne de caractère en JSON pour éviter des problèmes de désérialisation par la suite
                 * En effet, ils seront utils pour la désérialisation
                 */
                requestNode.putPOJO(JSONFieldsRequest.REQUESTED_FIELDS.getLabel(), requestedFields);
                requestNode.putPOJO(JSONFieldsRequest.REQUIRED_VALUES.getLabel(), requiredValues);
                //TODO : est-ce vraiment utile de le garder dans le cadre de mon UC ?
                requestNode.putPOJO(JSONFieldsRequest.REQUIRED_TESTS.getLabel(), requiredTests);

                //Les infos de la requête + L'objet sérialisé
                objectNode.putPOJO(JSONFieldsRequest.REQUEST_INFO.getLabel(), requestNode);
                objectNode.putPOJO(JSONFieldsRequest.SERIALIZED_OBJECT.getLabel(), serializedObjectNode);

            //Transformation
            requestSender = (requestSender == null) ? RequestSender.CLIENT : requestSender;
            objectNode.putPOJO(JSONFieldsRequest.REQUEST_SENDER.getLabel(), requestSender);
            //Transformation de de l'objet en String JSON
            javaobjectToJSONobj = objectMapper.writeValueAsString(objectNode);
        } catch (IOException e) {
            log.error("Cette erreur est survenue lors de la sérialisation de la requête :\n" + e.getMessage());
        }

        return javaobjectToJSONobj;
    }

    @SuppressWarnings("finally")
    //Récupére la valeur du noeud
    public static String getNodeValueOfJson(JSONFieldsRequest field, String json)
    {
        String result = "";

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode objectFromStringJSONNode = objectMapper.readTree(json);
            JsonNode dataNode = objectFromStringJSONNode.get(field.getLabel());
            result = dataNode.textValue();
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            return result;
        }
    }

}
