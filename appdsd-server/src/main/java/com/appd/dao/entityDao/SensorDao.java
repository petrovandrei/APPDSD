package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/*
CRUD pour capteur (DAO pour persistence des couches)
 */
public class SensorDao extends DAO<Sensor> {
    private static final Logger log;

    static {
        log = LoggerFactory.getLogger(SensorDao.class);
    }

    //super constructor
    public SensorDao(Connection connection)
    {
        super(connection, "SENSOR");
    }

    @Override
    public Sensor create(Sensor obj) {
        return null;
    }

    /*
    Méthode pour modifier un capteur : va être utile pour modifier un capteur et, ainsi, lui affecter ou enlever une localisation
     */
    @Override
    public void update(Sensor sensor) {
        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE SENSOR SET ID_LOCATION = ?, "
                                    + " WHERE ID = ?");
                    preparedStatement.setInt(1, sensor.getLocationId());
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

    /*
    Méthode pour renvoyer une liste de capteurs en fonction de l'id de leur localisation
     */
    public List<Sensor> getSensorsByLocation(int locationId){

       //la liste de sensors que l'on va renvoyer
        List<Sensor> sensors = new ArrayList<Sensor>();

        // On vérifie d'abord la connection
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
