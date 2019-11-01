package com.appd.socket;

import com.appd.connection.pool.implementation.DataSource;
import com.appd.util.Util;
import com.appd.util.UtilServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private Connection connection;

    private ServerSocket serverSocket;
    private static final int PORT = Integer.parseInt(Util.getPropertyValueFromApplicationProperties("server_port"));


    public Server() {
        connection = null;
    }


    public void start() {
        //log.info("Lancement du server version v" + UtilServer.getApplicationVersion() + " en cours...");

        DataSource.startConnectionPool();


        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {

                if (DataSource.getRemaningConnections() > 0) {

                    Socket socket = serverSocket.accept();
                    connection = DataSource.getConnection();
                    RequestHandler requestHandler = new RequestHandler(socket, connection);
                    Thread clientThread = new Thread(requestHandler);
                    clientThread.start();
                }

            }
        } catch (Exception e) {
            log.error("Server exception : " + e.getMessage());
            DataSource.closeConnectionPool();
        }
    }

}
