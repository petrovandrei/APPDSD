package com.appd.main;

import com.appd.socket.Server;

public class ServerMain {

    public static void main(String [] args)
    {
        Server server = new Server();
        server.start();
    }
}
