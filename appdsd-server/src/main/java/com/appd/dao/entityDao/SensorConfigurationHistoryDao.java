package com.appd.dao.entityDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.SensorConfigHistory;
import com.appd.enumeration.SensorAction;

public class SensorConfigurationHistoryDao extends DAO<SensorConfigHistory> {

    private static final Logger log = LoggerFactory.getLogger(SensorConfigurationHistoryDao.class);

    public SensorConfigurationHistoryDao(Connection connection) {
        super(connection, "SENSOR_CONFIGURATION_HISTORY");
    }

    @Override
    public SensorConfigHistory create(SensorConfigHistory obj) {
        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection
                            .prepareStatement("INSERT INTO "+ tableName +" (ID_SENSOR_CONFIGURATION, MEASURED_THRESHOLD, MIN_DANGER_THRESHOLD, MAX_DANGER_THRESHOLD, MEASUREMENT_DATE, END_ALERT_DATE, DESCRIPTION, ACTION_DONE)"
                                    + " VALUES (? , ? , ? , ? , ? , ? , ? , ?)", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1, obj.getIdSensorSource());
                    preparedStatement.setFloat(2, obj.getMeasuredThreshold());
                    preparedStatement.setFloat(3, obj.getMinDangerThreshlod());
                    preparedStatement.setFloat(4, obj.getMaxDangerThreshlod());
                    preparedStatement.setTimestamp(5, obj.getDate());
                    preparedStatement.setTimestamp(6, obj.getEndAlertDate());
                    preparedStatement.setString(7, obj.getDescription());
                    SensorAction action = obj.getActionDone();
                    if(action != null) {
                        preparedStatement.setString(8, action.name());
                    } else {
                        preparedStatement.setString(8, null);
                    }


                    preparedStatement.execute();
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    int lastCreatedId = 0;
                    if (rs.next()) {
                        lastCreatedId = rs.getInt(1);
                        obj.setIdHistory(lastCreatedId);
                    }
                } catch (Exception e) {
                    log.error("An error occurred during the creation of a location : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return obj;
        }
    }

    @Override
    public void update(SensorConfigHistory obj) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE " + tableName + " SET ACTION_DONE = ? , END_ALERT_DATE = ?  WHERE ID_HISTORY = ?");
            preparedStatement.setString(1, obj.getActionDone().name());
            preparedStatement.setTimestamp(2, obj.getEndAlertDate());
            preparedStatement.setInt(3, obj.getIdHistory());
            preparedStatement.execute();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(SensorConfigHistory obj) {
        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("DELETE FROM "+ tableName +" where ID_HISTORY=(?)");
                    preparedStatement.setInt(1, obj.getIdHistory());
                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("An error occurred during the delete of a location : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressWarnings("finally")
    @Override
    protected SensorConfigHistory getSingleValueFromResultSet(ResultSet rs) {
        SensorConfigHistory sensorConfigurationHistory = null;
        try {
            sensorConfigurationHistory = new SensorConfigHistory(rs.getInt("ID_HISTORY"), rs.getInt("ID_SENSOR_CONFIGURATION"), rs.getFloat("MEASURED_THRESHOLD"), rs.getFloat("MIN_DANGER_THRESHOLD"),rs.getFloat("MAX_DANGER_THRESHOLD"),
                    rs.getTimestamp("MEASUREMENT_DATE"), rs.getTimestamp("END_ALERT_DATE"), rs.getString("DESCRIPTION"), SensorAction.getValueOf(rs.getString("ACTION_DONE")));


        } catch (SQLException e) {
            log.error("An error occurred when getting one Flow Sensor from the resultSet : " + e.getMessage());
        }
        finally {
            return sensorConfigurationHistory;
        }
    }

}
