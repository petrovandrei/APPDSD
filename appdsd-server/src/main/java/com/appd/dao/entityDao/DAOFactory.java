package com.appd.dao.entityDao;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Location;
import com.appd.entity.Person;
import com.appd.entity.Sensor;
import com.appd.enumeration.RequestTypes;
import com.appd.exception.UnknownClassException;

import java.sql.Connection;
import java.util.List;

public class DAOFactory {

    @SuppressWarnings({ "rawtypes", "unchecked" })

    public static Object execute(Connection connection, Class<?> entityClass, RequestTypes requestTypes, Object object, List<String> fields, List<String> values, List<String> tests) throws Exception
    {

        DAO dao = null;


        if(entityClass.equals(Location.class))
            dao = new LocationDao(connection);
        else if(entityClass.equals(Sensor.class))
            dao = new SensorDao(connection);
        else if(entityClass.equals(Person.class))
            dao = new PersonDAO(connection);
        else
            throw new UnknownClassException(entityClass);


        Object result = null;


        switch(requestTypes)
        {

            case SELECT:
                result = dao.find(fields, values, tests);
                break;
            case INSERT:
                result = dao.create(entityClass.cast(object));
                break;

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
