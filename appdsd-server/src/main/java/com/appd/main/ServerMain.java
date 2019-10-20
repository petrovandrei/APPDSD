package com.appd.main;

import com.appd.socket.Server;

public class ServerMain {

    public static void main(String [] args)
    {
        Server server = new Server();
        // On démarre le serveur, créer les connections, le connection pool et les connections client
        //TODO : Démontrer la saturation du pool
        server.start();
    }
}
