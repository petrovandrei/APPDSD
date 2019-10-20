package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Location;
import com.appd.entity.Sensor;
import com.appd.enumeration.RequestTypes;
import com.appd.exception.UnknownClassException;

import java.sql.Connection;
import java.util.List;

/**
 * Classe pour associer le bon DAO, trouver le type de requête, renvoyer le résultat de la requête
 * Executer une requête
 */
public class DAOFactory {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    /**
     * Méthode pour executer une requete et renvoyer un objet
     */
    public static Object execute(Connection connection, Class<?> entityClass, RequestTypes requestTypes, Object object, List<String> fields, List<String> values, List<String> tests) throws Exception
    {
        //On initialise le DAO à null
        DAO dao = null;

        //On test la valeur de la classe et instancie au DAO équivalent
        //On soulève une UnknownClassException si la classe n'existe pas dans le projet
        if(entityClass.equals(Location.class))
            dao = new LocationDao(connection);
        else if(entityClass.equals(Sensor.class))
            dao = new SensorDao(connection);
        else
            throw new UnknownClassException(entityClass);

        //Si le bon DAO a été associé : le code va ici
        //Le résultat est initialisé à null
        Object result = null;

        //On teste le type de requête
        //Result prend alors la valeur que renvoie la requête
        switch(requestTypes)
        {
            //En R0 ce ne sera que SELECT
            case SELECT:
                result = dao.find(fields, values, tests);
                break;
            case INSERT:
                result = dao.create(entityClass.cast(object));
                break;
            //Fin du UC ce sera UPDATE
            case UPDATE:
                dao.update(entityClass.cast(object));
                break;
            case DELETE:
                dao.delete(entityClass.cast(object));
                break;
            default:
                throw new Exception("Le type de la requête ne correspond à aucun type existant...");
        }
        return result;

    }
}
