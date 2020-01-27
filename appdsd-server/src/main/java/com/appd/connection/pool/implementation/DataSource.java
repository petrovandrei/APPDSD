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
    public static synchronized Connection getConnection()
    {
        try {
            return connectionPool.getConnection();
        } catch (Exception e) {
            log.error("No connections are availabe. Check the virtual machines\n" + e.getMessage());
            return null;
        }
    }

    public static synchronized void putConnection(Connection connection)
    {
        connectionPool.putConnection(connection);
    }

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
