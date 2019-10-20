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

    /**
     * On démarre le pool et permet ainsi les connections clients
     */
    public void start()
    {
        log.info("Lancement du server version v" + UtilServer.getApplicationVersion() + " en cours...");
        //Affichage du server sur la console
        //System.out.println(UtilServer.getASCII("title.txt"));

        //On démarre le connectionPool
        DataSource.startConnectionPool();


        try{
            serverSocket = new ServerSocket(PORT);
            while(true)
            {

                if(DataSource.getRemaningConnections() > 0)
                {

                    /*
                     * La socket est utilisé pour les communications entre clients et server.
                     * Des connexions multiples peuvent être réalisées avec le même port mais pas les  mêmes instacnces de socket
                     */
                    Socket socket = serverSocket.accept();
                    connection = DataSource.getConnection();
                    /*
                     * Après une connexion server par le client
                     * Le client est associé à son propre thread
                     */
                    RequestHandler requestHandler  = new RequestHandler(socket, connection);
                    Thread clientThread = new Thread(requestHandler);
                    //On lance ce thread
                    clientThread.start();
                }

            }
        }
        catch(Exception e) {
            log.error("Server exception : " + e.getMessage());
            //On ferme le pool
            DataSource.closeConnectionPool();
        }
    }

}
