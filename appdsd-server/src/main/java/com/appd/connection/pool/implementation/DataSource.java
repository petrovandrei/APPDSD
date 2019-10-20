package com.appd.connection.pool.implementation;

import java.sql.Connection;
import java.sql.SQLException;

import com.appd.connection.pool.abstracts.InterfaceJDBCConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {

    private static final Logger log = LoggerFactory.getLogger(DataSource.class);
    private static InterfaceJDBCConnectionPool connectionPool = new JDBCConnectionPool();

    public DataSource() {}

    /**
     * We have add the keyword 'synchronized' in this method because this will prevent many users to use the same connection
     *
     * @return Connection :
     * 		connection of the connectionPool attribute
     */
    public static synchronized Connection getConnection()
    {
        try {
            return connectionPool.getConnection();
        } catch (Exception e) {
            log.error("The connections are not availabe. Check if the virtual machines are turned on :\n" + e.getMessage());
            return null;
        }
    }

    /**
     * Puts a connection in the connectionPool attribute
     *
     * @param connection
     */
    public static synchronized void putConnection(Connection connection)
    {
        connectionPool.putConnection(connection);
    }

    /**
     * Closes the attribute connectionPool
     */
    public static void closeConnectionPool()
    {
        connectionPool.closeAllConnections();
    }

    public static void startConnectionPool() {
        try {
            connectionPool.fillConnectionsList();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized int getRemaningConnections(){
        return connectionPool.getRemaningNumberOfConnections();
    }

}
