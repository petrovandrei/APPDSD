package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class LocationDao extends DAO<Location> {

    private static final Logger log;

    static {
        log = LoggerFactory.getLogger(LocationDao.class);
    }


    public LocationDao(Connection connection) {
        super(connection, "LOCATION");
    }


    @Override
    public Location create(Location location) {
        synchronized (lock) {
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection
                            .prepareStatement("INSERT INTO LOCATION (NAME, FLOOR,"
                                    + "POSITION_X, POSITION_Y, WIDTH, HEIGHT)"
                                    + " VALUES (? , ? , ? , ? , ? , ? )", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, location.getName());
                    preparedStatement.setInt(2, location.getFloor());
                    preparedStatement.setFloat(3, location.getPositionX());
                    preparedStatement.setFloat(4, location.getPositionY());
                    preparedStatement.setFloat(5, location.getWidth());
                    preparedStatement.setFloat(6, location.getHeight());
                    preparedStatement.execute();
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    int lastCreatedId = 0;
                    if (rs.next()) {
                        lastCreatedId = rs.getInt(1);
                        location.setIdLocation(lastCreatedId);
                    }
                } catch (Exception e) {
                    log.error("Une erreur a eu lieu lors de la création de la localisation : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return location;
        }
    }

    @Override
    public void update(Location location) {
        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE LOCATION SET NAME = ?, FLOOR = ?, "
                                    + " WHERE ID = ?");
                    preparedStatement.setString(1, location.getName());
                    preparedStatement.setInt(2, location.getFloor());
                    preparedStatement.setInt(3, location.getIdLocation());
                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("Une erreur a eu lieu lors de la modification de la localisation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(Location location) {
        synchronized (lock) {
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("DELETE FROM LOCATION where ID=(?)");
                    preparedStatement.setInt(1, location.getIdLocation());
                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("Une erreur est survenue durant la suppression de la localisation : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Location getSingleValueFromResultSet(ResultSet rs) {
        Location location = null;
        try {
            location = new Location(rs.getInt("ID"), rs.getString("NAME"), rs.getInt("FLOOR"), rs.getFloat("POSITION_X"), rs.getFloat("POSITION_Y"), rs.getFloat("WIDTH"), rs.getFloat("HEIGHT"));

        } catch (SQLException e) {
            log.error("Une erreur est survenue lors de la sélection d'une localisation en base : " + e.getMessage());
        } finally {
            return location;
        }
    }


}
