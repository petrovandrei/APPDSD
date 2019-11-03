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

public class GuiUtil {

    private static final Logger log = LoggerFactory.getLogger(GuiUtil.class);

    private static final String APPLICATION_VERSION = Util.getPropertyValueFromApplicationProperties("version");
    private static String serverVersion = "";

    public static void showNoConnectionMessage() {
        JOptionPane.showMessageDialog(null, ConnectionStates.NO_CONNECTION.getFrenchLabel() + "", "Erreur : Le serveur est momentanément indisponible.", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }


    public static String sendRequest(String jsonRequest) throws NoConnectionException, IOException, BadVersionException
    {
        String response = "";
        ClientSocket clientSocket = new ClientSocket();
        ConnectionStates state = clientSocket.start();

        if(state == ConnectionStates.SUCCESS || state == ConnectionStates.DEPRECATED_VERSION)
        {
            System.out.println("CONNECTION STATE = SUCCESS");
            if(state == ConnectionStates.DEPRECATED_VERSION)
            {
                String message = ConnectionStates.DEPRECATED_VERSION.getFrenchLabel() + "\n";
                message += "Use this version instead " + serverVersion;
                JOptionPane.showMessageDialog(null, message, "Version is obsolete", JOptionPane.INFORMATION_MESSAGE);
            }


            response = clientSocket.sendRequestToServer(jsonRequest);


            if(!jsonRequest.trim().equals(ConnectionStates.RESERVED.getCode().toString()))
            {
                String error = JsonUtil.getNodeValueOfJson(JSONFieldsRequest.ERROR_MESSAGE, response).trim();

                if(!error.equals(""))
                {
                    JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
                    throw new NoConnectionException();
                }


                log.debug("Server response :\n" + response);
            }

            return response;
        }
        else
        {
            showNoConnectionMessage();
            throw new NoConnectionException();
        }
    }




    public static String getApplicationVersion() {
        return APPLICATION_VERSION;
    }


    public static void setServerVersion(String serverVersion) {
        GuiUtil.serverVersion = serverVersion;
    }
}
