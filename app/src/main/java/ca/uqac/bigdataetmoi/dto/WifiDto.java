package ca.uqac.bigdataetmoi.dto;

public class WifiDto {
    public String ssid;
    public String name;
    public String date;

    public WifiDto(String ssid, String name, String date) {
        this.ssid = ssid;
        this.name = name;
        this.date = date;
    }
}
