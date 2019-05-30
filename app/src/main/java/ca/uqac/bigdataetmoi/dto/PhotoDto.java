package ca.uqac.bigdataetmoi.dto;

public class PhotoDto {
    public String img = "undefined";
    public String name = "undefined";
    public String datetime = "undefined";
    public String path = "undefined";

    public PhotoDto(String img, String name,  String datetime, String path) {
        this.img = img;
        this.name = name;
        this.datetime = datetime;
        this.path = path;
    }
}
