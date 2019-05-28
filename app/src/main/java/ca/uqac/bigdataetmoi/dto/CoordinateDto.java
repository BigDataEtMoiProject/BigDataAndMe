package ca.uqac.bigdataetmoi.dto;

public class CoordinateDto {
    private String longitude;
    private String latitude;
    private String date;

    public CoordinateDto(String longitude, String latitude, String date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }
}
