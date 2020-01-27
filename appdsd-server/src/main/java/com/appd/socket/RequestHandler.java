package com.appd.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.appd.alert.AlertHandler;
import com.appd.connection.pool.implementation.DataSource;
import com.appd.dao.entityDao.DAOFactory;
import com.appd.entity.Message;
import com.appd.entity.Person;
import com.appd.entity.SensorConfiguration;
import com.appd.enumeration.ConnectionStates;
import com.appd.enumeration.RequestSender;
import com.appd.enumeration.RequestTypes;
import com.appd.enumeration.SensorState;
import com.appd.util.JSONField;
import com.appd.util.JsonUtil;
import com.appd.util.Util;

import com.appd.util.UtilServer;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



    public class RequestHandler implements Runnable {

        private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

        //This reader will allow us to read the message received and so sent by the client
        private BufferedReader readFromClient;

        //This writer will allow us to send a response to the client
        private PrintWriter writeToClient;

        private AlertHandler alertHandler = new AlertHandler();

        private Socket socket;
        private Connection connection;
        //For the JSON
        private ObjectMapper mapper;
        //private AlertHandler alertHandler;
        private final int oneSecondMs = DateTimeConstants.MILLIS_PER_SECOND;
        private final String encodageType = Util.getPropertyValueFromApplicationProperties("encodage_type");

        public RequestHandler(Socket socket, Connection connection, AlertHandler alertHandler) {
            this.socket = socket;
            this.connection = connection;
            this.alertHandler = alertHandler;
            mapper = new ObjectMapper();
        }

        @Override
        public void run() {

            try
            {
                readFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), encodageType));
                writeToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encodageType), true);

                String[] ipAddressAndVersion = readFromClient.readLine().split(Util.getVersionSplitter());

                //log.info("Client connected with the IP " + ipAddressAndVersion[0]);

                //Check client application version
                String clientVersion = ipAddressAndVersion[1];
                String serverVersion = UtilServer.getApplicationVersion();

                //Check if the client has the same version than the server
                if(!clientVersion.equalsIgnoreCase(serverVersion))
                {
                    writeToClient.println(ConnectionStates.DEPRECATED_VERSION.getCode() + "v" + serverVersion);
                }
                else
                {
                    writeToClient.println("");
                }

                //Handle client request if he has the good version of the application
                String requestOfClient = readFromClient.readLine();
                String reservedConnectionCode = ConnectionStates.RESERVED.getCode().toString();
                String ipAddress = ipAddressAndVersion[0];

                //Checks if the client (= super user) wants to reserve the connection
                if(requestOfClient.trim().equalsIgnoreCase(reservedConnectionCode))
                {
                    int reservedTimeInMilliseconds = NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("reserved_time_ms"));
                    String reservedTime = (new Integer(reservedTimeInMilliseconds / oneSecondMs)).toString();
                    log.info("A client has reserved a connection for " + reservedTime + " sec\n");
                    String message = reservedTime + " sec-" + reservedTime;
                    writeToClient.println(message);
                    //Sleeps the Thread in order to make the connection no accessible by another person
                    Thread.sleep(reservedTimeInMilliseconds);
                    log.info("A client has release its reserved connection !");
                }
                else
                {
                    JsonNode json = mapper.readTree(requestOfClient);
                    RequestSender requestSender = RequestSender.getValueOf(JsonUtil.getJsonNodeValue(JSONField.REQUEST_SENDER, requestOfClient));

                    if(requestSender == RequestSender.CLIENT) {
                        log.info("Request received from the client :\n" + JsonUtil.indentJsonOutput(requestOfClient) + "\n");
                        //Person newPerson = new Person();
                        //log.info(newPerson.toString());
                        //log.info("Request received from the client "+ipAddress+":\n" + requestOfClient + "\n");
                        String responseToClient = executeClientRequest(json);
                        log.info("Response to the client "+ipAddress+" :\n" + responseToClient + "\n");
                        //log.info("Response to the client :\n" + JsonUtil.indentJsonOutput(responseToClient) + "\n");
                        writeToClient.println(responseToClient);
                    }
                    else if(requestSender == RequestSender.SENSOR) {
                        Message message = (Message)getObjectFromJson(json);
                        alertHandler.processMessage(message);
                        writeToClient.println("");
                    }
                    else if(requestSender == RequestSender.CLIENT_FOR_SENSOR_STATE) {
					/*SensorState state = SensorState.valueOf(JsonUtil.getJsonNodeValue(JSONField.CACHE_SENSOR_STATE, requestOfClient));
					List<SensorConfiguration> sensorConfigurations = alertCenter.getCacheSensorsByState(state);
					String serializedObjects = JsonUtil.serializeObject(sensorConfigurations, SensorConfiguration.class, "");
					writeToClient.println(serializedObjects);*/
                        Map<SensorState, List<SensorConfiguration>> map =  alertHandler.getActiveSensorsByState();
                        String serializedObject = JsonUtil.serializeCacheSensorsMap(map);
                        writeToClient.println(serializedObject);
                    }
                }

            }
            catch (Exception e)
            {
                log.error("Exception : The client is disconnected");
            }
            finally
            {
                exit();
            }
        }


        @SuppressWarnings("finally")
        public String executeClientRequest(JsonNode json)
        {
            String result = "";

            try
            {
                // JSON Node containing the request info
                JsonNode requestNode = json.get(JSONField.REQUEST_INFO.getLabel());
                String requestEntity = requestNode.get(JSONField.REQUESTED_ENTITY.getLabel()).textValue();
                Class<?> entityClass = Class.forName(requestEntity);

                // The fields we wants to filter
                String fieldsStringFromJson = requestNode.get(JSONField.REQUESTED_FIELDS.getLabel()).toString();
                // The values of the filters we want to filter
                String valuesStringFromJson = requestNode.get(JSONField.REQUIRED_VALUES.getLabel()).toString();
                // The tests
                String testsStringFromJson = requestNode.get(JSONField.REQUIRED_TESTS.getLabel()).toString();

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

                RequestTypes requestType = RequestTypes.getRequestTypeValue(requestNode.get(JSONField.REQUEST_TYPE.getLabel()).textValue());

                Object objectResult = DAOFactory.execute(connection, entityClass, requestType, deserializedObject, fields, requiredValues, tests);

                result = JsonUtil.serializeObject(objectResult, entityClass, "");



            }
            catch (Exception e)
            {
                log.error("An error occured during the execution of the client request :\n" + e.getMessage());
            }
            finally
            {
                return result;
            }

        }

        private Object getObjectFromJson(JsonNode json)
        {
            JsonNode serializedObjectNode = json.get(JSONField.SERIALIZED_OBJECT.getLabel());
            if(!serializedObjectNode.isNull())
                return JsonUtil.deserializeObject(serializedObjectNode.toString());
            return null;
        }

        /**
         * This method will give back the connection to the pool and close the socket
         */
        private void exit()
        {
            try
            {
                //Give back to connection to the pool
                DataSource.putConnection(connection);
                this.connection = null;
                socket.close();
                readFromClient.close();
                writeToClient.close();
            }
            catch (IOException e)
            {
                log.error("An error occured during the closure of a socket : " + e.getMessage());
            }
        }
    }
