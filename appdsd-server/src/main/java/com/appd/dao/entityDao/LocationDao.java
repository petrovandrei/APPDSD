package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/*
CRUD pour localisation (DAO pour persistence des couches)
 */
public class LocationDao extends DAO<Location> {

    private static final Logger log;

    static {
        log = LoggerFactory.getLogger(LocationDao.class);
    }

    //super constructor
    public LocationDao(Connection connection)
    {
        super(connection, "LOCATION");
    }

    /*
    Créer une localisation : pas nécessairement utile si on part du principe que la map est fixe
     */
    @Override
    public Location create(Location location) {
        synchronized (lock) {
            // Vérifier que la connection est disponible (différente de null) avant de commencer
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

    /*
    Méthode pour modifier une localisation, à voir si j'ai le temps mais pas obligatoire dans le usecase
    Je pars du principe qu'on ne peut modifier que le nom et l'étage
    TODO : ajouter un bouton pour modifier une localisation
     */
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

    /*
    Pour supprimer une localisation : pas nécessairement utile si on part du principe que la map est fixe
     */
    @Override
    public void delete(Location location) {
        synchronized (lock) {
            // Pareil on vérifie toujours que la connection est différente de null avant de commencer
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
        }
        finally {
            return location;
        }
    }


}
