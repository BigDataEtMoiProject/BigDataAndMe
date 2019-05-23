package ca.uqac.bigdataetmoi.dto;

public class CoordinatesDto {
    private String longitude;
    private String latitude;
    private String date;

    public CoordinatesDto(String longitude, String latitude, String date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }
}
