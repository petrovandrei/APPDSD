package com.appd.socket;

import com.appd.enumeration.ConnectionStates;
import com.appd.exception.BadVersionException;
import com.appd.util.Util;
import com.appd.util.GuiUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientSocket {

    private static final Logger log = LoggerFactory.getLogger(ClientSocket.class);
    private final String SERVER_IP = Util.getPropertyValueFromApplicationProperties("server_ip");
    private final int PORT = Integer.parseInt(Util.getPropertyValueFromApplicationProperties("server_port"));
    private BufferedReader readFromServer;
    private PrintWriter writeToServer;


    private final int TIMEOUT = NumberUtils.toInt(Util.getPropertyValueFromApplicationProperties("server_response_limit"));

    private Socket socket;
    private InetAddress ipAddress;
    private final String versionSplitter = Util.getVersionSplitter();
    private final String encodageType = Util.getPropertyValueFromApplicationProperties("encodage_type");

    public ClientSocket() {

    }


    public ConnectionStates start()
    {
        ConnectionStates connectionState = ConnectionStates.NO_CONNECTION;

        System.out.println("STATE CONNECTION = NO");

        try
        {
            System.out.println(SERVER_IP + " " + PORT);
            socket = new Socket(SERVER_IP, PORT);
            System.out.println(SERVER_IP + " " + PORT);
            System.out.println("NEW SOCKET");

            socket.setSoTimeout(TIMEOUT);

            readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), encodageType));
            writeToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encodageType), true);
            ipAddress = InetAddress.getLocalHost();
            writeToServer.println(ipAddress + versionSplitter + GuiUtil.getApplicationVersion());

            String[] serverCheck = readFromServer.readLine().split(versionSplitter);
            if(serverCheck.length >= 2)
            {
                String code = serverCheck[0];
                String serverVersion = serverCheck[1];
                if(code.equalsIgnoreCase(ConnectionStates.DEPRECATED_VERSION.getCode().toString()))
                {
                    GuiUtil.setServerVersion(serverVersion);
                    throw new BadVersionException(serverVersion);
                }

            }
            connectionState = ConnectionStates.SUCCESS;
        }
        catch(BadVersionException e)
        {
            log.error(e.getMessage());
            connectionState = ConnectionStates.DEPRECATED_VERSION;
        }
        catch (SocketTimeoutException e)
        {
            log.error("timeout : " + e.getMessage() + ".\n No connection to sever");
            exit();
        }
        catch (Exception e)
        {
            log.error("Server logout : " + e.getMessage());
            exit();
        }
        finally
        {
            return connectionState;
        }
    }


    public String sendRequestToServer(String requestToSendToServer) throws IOException
    {
        String responseFromServer = "";

        if(requestToSendToServer.trim().equals(ConnectionStates.RESERVED.getCode().toString()))
            log.info("New connection reserved");
        writeToServer.println(requestToSendToServer);
        responseFromServer = readFromServer.readLine();
        exit();
        return responseFromServer;
    }

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
