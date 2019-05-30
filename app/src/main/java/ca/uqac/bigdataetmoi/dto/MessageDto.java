package ca.uqac.bigdataetmoi.dto;

public class MessageDto {
    public String message;
    public String phone;
    public String date;

    public MessageDto(String phone, String date, String message){
        this.message = message;
        this.phone = phone;
        this.date = date;
    }
}

