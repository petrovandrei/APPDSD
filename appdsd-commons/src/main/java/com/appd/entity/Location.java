package com.appd.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

public class Location {

    //@JsonProperty : permet d'ajouter les noms des propriétés en JSON
    @JsonProperty("id")
    private int idLocation;
    @JsonProperty("name")
    private String name;
    @JsonProperty("floor")
    private int floor;

    //annotation pour le texte dans les fichiers JSON
    @JsonProperty("position_x")
    private float positionX;
    @JsonProperty("position_y")
    private float positionY;
    @JsonProperty("width")
    private float width;
    @JsonProperty("height")
    private float height;

    public Location(int idLocation, String name, int floor, float positionX, float positionY, float width, float height) {
        this.idLocation = idLocation;
        this.name = name;
        this.floor = floor;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

    public Location() {
        super();
    }

    @Override
    public String toString() {
        return "Location{" +
                "identifiant=" + idLocation +
                ", nom='" + name + '\'' +
                ", étage=" + floor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return idLocation == location.idLocation &&
                floor == location.floor &&
                Float.compare(location.positionX, positionX) == 0 &&
                Float.compare(location.positionY, positionY) == 0 &&
                Float.compare(location.width, width) == 0 &&
                Float.compare(location.height, height) == 0 &&
                Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLocation, name, floor, positionX, positionY, width, height);
    }

    @JsonGetter("id")
    public int getIdLocation() {
        return idLocation;
    }

    @JsonSetter("id")
    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("floor")
    public int getFloor() {
        return floor;
    }

    @JsonSetter("floor")
    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
