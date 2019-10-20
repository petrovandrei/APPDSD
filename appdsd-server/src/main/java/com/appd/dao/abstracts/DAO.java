package com.appd.dao.abstracts;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite pour les DAO
 * CRUD
 * @param <T>
 */
public abstract class DAO<T> {

    private static final Logger log = LoggerFactory.getLogger(DAO.class);
    protected String tableName;
    protected Connection connection;
    protected final Object lock = new Object();


    //Construcor

    /**
     * @param connection --> Connectoin
     * @param tableName --> nom de la table
     */
    public DAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    /**
     * Créer l'objet en base : ne sera pas utile pour mon UC
     * @param obj
     * @return l'objet inséré en base
     */
    public abstract T create(T obj);

    /**
     * Update l'obj
     * @param obj
     */
    public abstract void update(T obj);

    /**
     * Supprimer un objet
     * @param obj
     */
    public abstract void delete(T obj);

    /**
     * Lire un objet en bae
     * @param fields : the fields we want to filter
     * @param values : the values required for the fields
     * @return la liste d'éléments demandés
     */
    public synchronized List<T> find(List<String> fields, List<String> values, List<String> tests){
        List<T> elements = new ArrayList<T>();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM " + tableName;
                boolean isInnerJoin = false;

                //On passe la resuete à la connection
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                //On execute la requete et récupère le rseult
                ResultSet rs = preparedStatement.executeQuery();
                T element;
                //On li toutes les valeurs récupèrée et on les ajoute à notre liste
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

    /**
     * Méthode pour permettre les quote dans les requêtes SQL
     * @param value
     * @return La valeur avec les guillements (si ce n'est pas un nombre)
     */
    protected String addNecessaryQuotes(String value)
    {
        //Dépendance apache dans un POM
        //TODO : le mettre dans le parent
        if(!NumberUtils.isParsable(value) && !value.contains("cast"))
            value = "'" + value + "'";
        return value;
    }


    //Récupération d'une seule valeure et non liste
    protected abstract T getSingleValueFromResultSet(ResultSet rs);

    public String getTableName() {
        return tableName;
    }

}
