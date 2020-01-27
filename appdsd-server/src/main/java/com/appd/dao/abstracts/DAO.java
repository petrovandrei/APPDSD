package com.appd.dao.abstracts;

import java.sql.Connection;
 import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.dao.entityDao.SensorConfigurationDao;


public abstract class DAO<T> {

    private static final Logger log = LoggerFactory.getLogger(DAO.class);
    protected String tableName;
    protected final Object lock = new Object();
    protected Connection connection;

    public DAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    /**
     * Creates the object in the database
     * @param obj
     * @return the object with its id retrieved from the database
     */
    public abstract T create(T obj);

    /**
     * Updates an object
     * @param obj
     */
    public abstract void update(T obj);

    /**
     * Deletes an object
     * @param obj
     */
    public abstract void delete(T obj);

    /**
     * Finds the objects in the database
     * @param fields : the fields we want to filter
     * @param values : the values required for the fields
     * @return
     */
    public synchronized List<T> find(List<String> fields, List<String> values, List<String> tests){
        List<T> elements = new ArrayList<T>();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM " + tableName;
                boolean isInnerJoin = false;
               /* if(tableName.equalsIgnoreCase(SensorConfigurationDao.getFinalTableName()))
                {
                    isInnerJoin = true;
                    sql += " table1 inner join SENSOR table2 on table1.ID_SENSOR = table2.ID_SENSOR";
                }*/
                sql += getRequestFilters(fields, values, tests, isInnerJoin);
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery();
                T element;
                while (rs.next()) {
                    element = getSingleValueFromResultSet(rs);
                    if (element != null) {
                        elements.add(element);
                    }
                }
            } catch (Exception e) {
                log.error("An error occurred when finding all of " + tableName + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return elements;

    }

    /**
     * Allows to add quote if needeed in the sql request
     * @param value
     * @return the value with quote if it is not a number
     */
    protected String addNecessaryQuotes(String value)
    {
        if(!NumberUtils.isParsable(value) && !value.contains("cast"))
            value = "'" + value + "'";
        return value;
    }


    /**
     * Allows to specified the constraints in the request
     * @param fields
     * @param values
     * @return
     */
    protected String getRequestFilters(List<String> fields, List<String> values, List<String> tests, boolean isInnerJoin)
    {
        String sql = "";

        if(fields != null && values != null)
        {
            int fieldsListSize = fields.size();
            if(fieldsListSize >= 1 && fieldsListSize == values.size())
            {
                if(tests == null || tests.size() != fieldsListSize) {
                    //log.info("All elements of the tests list will be replaced by \"=\" because the tests list has not the same size than the values and fields list");
                    String[] testsArray = new String[fieldsListSize];
                    Arrays.fill(testsArray, "=");
                    tests = Arrays.asList(testsArray);
                }

                sql += " WHERE ";
                int i;
                String alias = (isInnerJoin) ? "table1." : "";
                for(i = 0; i < fieldsListSize; i++) {
                    String fieldName = fields.get(i);
                    fieldName = (fieldName.equalsIgnoreCase("ID_SENSOR")) ? alias + fieldName : fieldName;
                    sql += fieldName + tests.get(i) + addNecessaryQuotes(values.get(i));
                    sql += (i < fieldsListSize - 1) ? " AND " : "";
                }
            }
        }
        return sql;
    }

    protected abstract T getSingleValueFromResultSet(ResultSet rs);

    public String getTableName() {
        return tableName;
    }

}
