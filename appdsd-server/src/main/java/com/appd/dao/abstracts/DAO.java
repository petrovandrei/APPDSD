package com.appd.dao.abstracts;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class DAO<T> {

    private static final Logger log = LoggerFactory.getLogger(DAO.class);
    protected String tableName;
    protected Connection connection;
    protected final Object lock = new Object();


    //Construcor


    public DAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    public abstract T create(T obj);


    public abstract void update(T obj);


    public abstract void delete(T obj);


    public synchronized List<T> find(List<String> fields, List<String> values, List<String> tests){
        List<T> elements = new ArrayList<T>();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM " + tableName;
                boolean isInnerJoin = false;


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
                log.error("Une erreur est survenue lors de la recherche des éléments de la table : " + tableName + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return elements;
    }


    protected String addNecessaryQuotes(String value)
    {


        if(!NumberUtils.isParsable(value) && !value.contains("cast"))
            value = "'" + value + "'";
        return value;
    }

    protected abstract T getSingleValueFromResultSet(ResultSet rs);

    public String getTableName() {
        return tableName;
    }

}
