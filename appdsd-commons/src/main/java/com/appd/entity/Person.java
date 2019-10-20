package com.appd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Person {

    @JsonProperty("id")
    private int idPerson;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("role")
    private String role;
    @JsonProperty("password")
    private String password;


    public Person() {

    }

    public Person(int idPerson, String lastName, String firstName, String password, String role
                  ) {
        this.idPerson = idPerson;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Person{" +
                "idPerson=" + idPerson +
                ", nom de famille='" + lastName + '\'' +
                ", prenom='" + firstName + '\'' +
                ", role='" + role + '\'' +
                ", mdp='" + password + '\'' +
                '}';
    }

    public String toStringPartiel() {
        return "Person{" +
                "idPerson=" + idPerson +
                ", nom de famille='" + lastName + '\'' +
                ", prenom='" + firstName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return idPerson == person.idPerson &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(role, person.role) &&
                Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPerson, lastName, firstName, role, password);
    }


    //getters & setters

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


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
