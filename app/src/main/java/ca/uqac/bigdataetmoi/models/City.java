package ca.uqac.bigdataetmoi.models;

public class City {
    public String name = "undefined";
    public String longitude = "undefined";
    public String latitude = "undefined";
    public int numberOfCoordinatesLogged = 0;

    public City(String name, String longitude, String latitude, int numberOfCoordinatesLogged) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numberOfCoordinatesLogged = numberOfCoordinatesLogged;
    }

    public int getNumberOfCoordinatesLogged() {
        return numberOfCoordinatesLogged;
    }
}
