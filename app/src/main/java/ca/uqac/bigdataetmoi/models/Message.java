package ca.uqac.bigdataetmoi.models;

public class Message {
    public String message;
    public String phone;
    public String date;

    public Message(String phone, String date, String message){
        this.message = message;
        this.phone = phone;
        this.date = date;
    }
}

