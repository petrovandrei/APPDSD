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
import com.appd.entity.Person;


public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);


    private BufferedReader readFromClient;


    private PrintWriter writeToClient;

    private Socket socket;
    private Connection connection;
    private ObjectMapper mapper;

    private final int oneSecondMs = DateTimeConstants.MILLIS_PER_SECOND;

    private final String encodageType = Util.getPropertyValueFromApplicationProperties("encodage_type");


    public RequestHandler(Socket socket, Connection connection) {
        this.socket = socket;
        this.connection = connection;
        mapper = new ObjectMapper();
    }

    @Override
    public void run() {

        try {
            readFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), encodageType));
            writeToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encodageType), true);

            String[] ipAddressAndVersion = readFromClient.readLine().split(Util.getVersionSplitter());


            String clientVersion = ipAddressAndVersion[1];
            String serverVersion = UtilServer.getApplicationVersion();
            if (!clientVersion.equalsIgnoreCase(serverVersion)) {
                writeToClient.println(ConnectionStates.DEPRECATED_VERSION.getCode() + "v." + serverVersion);
            } else {
                writeToClient.println("");
            }

            String requestOfClient = readFromClient.readLine();
            String reservedConnectionCode = ConnectionStates.RESERVED.getCode().toString();
            String ipAddress = ipAddressAndVersion[0];


            if (requestOfClient.trim().equalsIgnoreCase(reservedConnectionCode)) {
                int reservedTimeInMilliseconds = NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("reserved_time_ms"));
                String reservedTime = (new Integer(reservedTimeInMilliseconds / oneSecondMs)).toString();
                log.info("A client reserved a connection for " + reservedTime + " sec\n");
                String message = reservedTime + " sec-" + reservedTime;
                writeToClient.println(message);
                Thread.sleep(reservedTimeInMilliseconds);
                log.info("Connection already reserved");
            } else {
                JsonNode json = mapper.readTree(requestOfClient);
                RequestSender requestSender = RequestSender.getClientSender(JsonUtil.getNodeValueOfJson(JSONFieldsRequest.REQUEST_SENDER, requestOfClient));

                if (requestSender == RequestSender.CLIENT) {
                    log.info("Request from client " + ipAddress + " :\n" + requestOfClient + "\n");
                    String responseToClient = executeClientRequest(json);
                    log.info("Server request to Client " + ipAddress + " :\n" + responseToClient + "\n");
                    writeToClient.println(responseToClient);
                }


            }

        } catch (Exception e) {
            log.error("Error: Client disconnected");
        } finally {

            exit();
        }
    }


    public String executeClientRequest(JsonNode json) {

        String result = "";

        try {

            JsonNode requestNode = json.get(JSONFieldsRequest.REQUEST_INFO.getLabel());
            String requestEntity = requestNode.get(JSONFieldsRequest.REQUESTED_LABEL_CLASS.getLabel()).textValue();
            Class<?> entityClass = Class.forName(requestEntity);


            String fieldsStringFromJson = requestNode.get(JSONFieldsRequest.REQUESTED_FIELDS.getLabel()).toString();

            String valuesStringFromJson = requestNode.get(JSONFieldsRequest.REQUIRED_VALUES.getLabel()).toString();

            String testsStringFromJson = requestNode.get(JSONFieldsRequest.REQUIRED_TESTS.getLabel()).toString();

            List<String> fields = null;
            List<String> requiredValues = null;
            List<String> tests = null;

            if (fieldsStringFromJson != null && valuesStringFromJson != null) {
                fields = mapper.readValue(fieldsStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                requiredValues = mapper.readValue(valuesStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                tests = mapper.readValue(testsStringFromJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            }

            Object deserializedObject = getObjectFromJson(json);

            RequestTypes requestTypes = RequestTypes.getRequestTypeValue(requestNode.get(JSONFieldsRequest.REQUEST_TYPE_CRUD.getLabel()).textValue());

            Object objectResult = DAOFactory.execute(connection, entityClass, requestTypes, deserializedObject, fields, requiredValues, tests);

            result = JsonUtil.serializeObject(objectResult, entityClass, "");


        } catch (Exception e) {
            log.error("Error while executing request :\n" + e.getMessage());
        } finally {

            return result;
        }

    }

    private Object getObjectFromJson(JsonNode json) {
        JsonNode serializedObjectNode = json.get(JSONFieldsRequest.SERIALIZED_OBJECT.getLabel());
        if (!serializedObjectNode.isNull())
            return JsonUtil.deserializeObject(serializedObjectNode.toString());
        return null;
    }

    private void exit() {
        try {
            DataSource.putConnection(connection);
            this.connection = null;
            socket.close();
            readFromClient.close();
            writeToClient.close();
        } catch (IOException e) {
            log.error("Error during closing a connection : " + e.getMessage());
        }
    }
}
