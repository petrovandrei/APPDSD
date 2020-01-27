package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class SensorDao extends DAO<Sensor> {
    private static final Logger log;

    static {
        log = LoggerFactory.getLogger(SensorDao.class);
    }

    //super constructor
    public SensorDao(Connection connection) {
        super(connection, "SENSOR");
    }


    public static int createSensor(Sensor obj, Connection connection) {
        /**try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO SENSOR (TYPE, MAC_ADDRESS,SERIAL_NUMBER)"
                            + "VALUES (?,?,?) ", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, obj.getType());
            preparedStatement.setString(2, obj.getMacAddress());
            preparedStatement.setString(3, obj.getSerialNumber());
            //preparedStatement.setFloat(4, obj.getHardwareVersion());
            //preparedStatement.setFloat(5, obj.getSoftwareVersion());
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return id;
            }
        } catch (Exception e) {
            log.error("An error occurred during the creation of a sensor : " + e.getMessage());
            e.printStackTrace();
        }
        return 0; **/
        return 0;
    }

    @Override
    public Sensor create(Sensor obj) {
        return null;
    }

    @Override
    public void update(Sensor sensor) {
        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE SENSOR SET ID_LOCATION = ?, "
                                    + " WHERE ID = ?");
                    /*preparedStatement.setInt(1, sensor.getLocationId());*/
                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("An error occurred during the update of a sensor : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(Sensor obj) {

    }

    @Override
    protected Sensor getSingleValueFromResultSet(ResultSet rs) {
        return null;
    }

    public List<Sensor> getSensorsByLocation(int locationId) {


        List<Sensor> sensors = new ArrayList<Sensor>();

        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * from SENSOR  "
                                + " WHERE ID_LOCATION = ?");
                preparedStatement.setInt(1, locationId);
                preparedStatement.execute();
                ResultSet rs = preparedStatement.executeQuery();
                Sensor sensor;
                while (rs.next()) {
                    sensor = getSingleValueFromResultSet(rs);
                    if (sensor != null) {
                        sensors.add(sensor);
                    }
                }
            } catch (Exception e) {
                log.error("Cette erreur est survenue lors de la sélection des capteurs : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return sensors;
    }
}
