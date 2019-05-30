package ca.uqac.bigdataetmoi.models;

public class Message {
    public String message = "undefined";
    public String phone = "undefined";
    public String date = "undefined";

    public Message (String phone, String date, String message){
        this.phone = phone;
        this.date = date;
        this.message = message;
    }
}

