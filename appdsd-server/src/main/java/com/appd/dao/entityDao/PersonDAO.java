package com.appd.dao.entityDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.dao.abstracts.DAO;
import com.appd.entity.Person;
import com.appd.enumeration.UserProfile;

public class PersonDAO extends DAO<Person>{

    private static final Logger log = LoggerFactory.getLogger(PersonDAO.class);

    public PersonDAO(Connection connection)
    {
        super(connection, "PERSON");
    }

    public Person create(Person person) {

        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO PERSON (LAST_NAME, FIRST_NAME, ROLE, PASSWORD) "
                                    + "VALUES (? , ?, ? , ?)", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, person.getLastName());
                    preparedStatement.setString(2, person.getFirstName());
                    preparedStatement.setString(3, person.getUserProfile().name());
                    preparedStatement.setString(4, person.getPassword());
                    preparedStatement.execute();
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    int lastCreatedId = 0;
                    if (rs.next()) {
                        lastCreatedId = rs.getInt(1);
                        person.setIdPerson(lastCreatedId);
                    }
                } catch (Exception e) {
                    log.error("An error occurred during the creation of a person : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return person;
        }

    }

    @Override
    public void delete(Person obj){

        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("DELETE FROM PERSON where id=(?)");
                    preparedStatement.setInt(1, obj.getIdPerson());

                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("An error occurred during the creation of a person : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(Person person) {

        synchronized (lock) {
            // Checks if the connection is not null before using it
            if (connection != null) {
                try {
                    PreparedStatement preparedStatement = connection
                            .prepareStatement("UPDATE PERSON SET LAST_NAME = ?, FIRST_NAME = ?, "
                                    + "ROLE = ?, PASSWORD = ? WHERE id =" + person.getIdPerson());
                    preparedStatement.setString(1, person.getLastName());
                    preparedStatement.setString(2, person.getFirstName());
                    preparedStatement.setString(3, person.getUserProfile().name());
                    preparedStatement.setString(4, person.getPassword());
                    preparedStatement.setInt(1, person.getIdPerson());
                    preparedStatement.execute();
                } catch (Exception e) {
                    log.error("An error occurred during the update of a person : " + e.getMessage());
                }
            }
        }

    }

    @SuppressWarnings("finally")
    @Override
    protected Person getSingleValueFromResultSet(ResultSet rs) {
        Person person = null;
        try {
            person = new Person(rs.getInt("ID"),rs.getString("LAST_NAME"),rs.getString("FIRST_NAME"),
                    UserProfile.valueOf(rs.getString("ROLE")),rs.getString("PASSWORD"),  rs.getTimestamp("ARRIVAL_DATE"));
        } catch (SQLException e) {
            log.error("An error occurred when getting one Person from the resultSet : " + e.getMessage());
        }
        finally {
            return person;
        }
    }

}