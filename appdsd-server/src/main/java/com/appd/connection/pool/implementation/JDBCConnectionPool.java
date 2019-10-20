package com.appd.connection.pool.implementation;

import com.appd.connection.pool.abstracts.InterfaceJDBCConnectionPool;
import com.appd.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

public class JDBCConnectionPool implements InterfaceJDBCConnectionPool{

    private Vector<Connection> connections;
    private static final Logger log = LoggerFactory.getLogger(JDBCConnectionPool.class);
    private final String URL             =  Util.getPropertyValueFromApplicationProperties("url_prod");
    private final String USER            =  Util.getPropertyValueFromApplicationProperties("first_name");
    private final String PASSWORD =  Util.getPropertyValueFromApplicationProperties("password");
    private int numberOfConnections;
    private int numberOfConnectionsCreated;


    public JDBCConnectionPool() {
        connections = new Vector<Connection>();
        numberOfConnectionsCreated = 0;
        log.info("Database URL :\n" + URL);
        //Mieux pour démontrer la saturation du pool !
        numberOfConnections = 3;

        log.info(numberOfConnections + " connection(s) should be put inside the connection pool.");
    }


    public void fillConnectionsList() throws SQLException {

        System.out.print("[");
        for (int i = 0; i < numberOfConnections; i++ )
        {
            Connection createdConnection = this.createConnection();
            if(createdConnection != null) {
                connections.addElement(createdConnection);
                System.out.print("=");
                //log.info("A connection has been created and is being added to the pool. (" + ( (i+1) + "/" + numberOfConnections) + ")" );
            }
            else {
                log.error("An error occurs during the creation of a connection because the connection equals to null !");
                /*
                 * An error occured, so we will stop the creation of the connection
                 * in order to avoid other problems
                 */
                throw new SQLException("A connection is equal to null !");
            }
        }
        System.out.println("] " + numberOfConnectionsCreated + " connection(s) created");
        displayConnectionPoolState();
    }

    public Connection getConnection() throws Exception {
        if(!connections.isEmpty())
        {
            Connection connection = connections.lastElement();
            connections.removeElement(connection);
            //log.info("A connection is being retrieved from the connection pool.");
            //displayConnectionPoolState();
            return connection;
        }
        else
        {
            throw new Exception("There are no connections left in the connection pool ! Please try later.");
        }
    }

    public void putConnection(Connection connection) {
        if(connection != null)
        {
            connections.addElement(connection);
            //log.info("A connection is being added to the connection pool.");
        }
        //displayConnectionPoolState();

    }

    public void closeAllConnections() {
        for(Connection connection : connections)
        {
            try {
                if(!connection.isClosed())
                {
                    connection.close();
                    log.info("A connection has been closed.");
                }
            } catch (SQLException e) {
                log.error("An error occurs during the closing of the connection :\n" + e.getMessage());
            }
        }
    }


    public Vector<Connection> getConnections() {
        return connections;
    }


    private Connection createConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            numberOfConnectionsCreated++;
        } catch (SQLException e) {
            log.error("A SQL Exception has been raised during the creation of a connection :\n" + e.getMessage());
        }
        return connection;
    }

    /*
    Connections restantes
     */
    public synchronized int getRemaningNumberOfConnections(){
        return connections.size();
    }


    /*
    Combien de connections il reste --> l'afficher
     */
    private void displayConnectionPoolState()
    {
        String state = "Connection(s) in the pool : " + getRemaningNumberOfConnections() + "/" + numberOfConnectionsCreated;
        log.info(state);
    }




}
