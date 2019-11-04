package com.appd.entity;

import com.appd.enumeration.UserProfile;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.Timestamp;
import java.util.Objects;

public class Person {

    @JsonProperty("id")
    private int idPerson;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("user_profile")
    private UserProfile userProfile;
    @JsonProperty("password")
    private String password;
    @JsonProperty("creation_date")
    private Timestamp creationDate;


    public Person(int id, String last_name, String first_name, UserProfile role, String password, java.sql.Timestamp arrival_date) {

    }
    public Person(){}

    public Person(int idPerson, String lastName, String firstName, UserProfile userProfile, String password) {
        this.idPerson = idPerson;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userProfile = userProfile;
        this.password = password;
        this.creationDate = creationDate;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + idPerson;
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((userProfile == null) ? 0 : userProfile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (idPerson != other.idPerson)
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (userProfile != other.userProfile)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Person [idPerson=" + idPerson + ", lastName=" + lastName + ", firstName=" + firstName + ", userProfile="
                + userProfile + ", password=" + password + ", creationDate=" + creationDate + "]";
    }


}
