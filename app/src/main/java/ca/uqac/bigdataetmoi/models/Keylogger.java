package ca.uqac.bigdataetmoi.models;


public class Keylogger {
     public String logged;
     public String datetime;

    public Keylogger(String logged, String datetime) {
         this.logged = logged;
         this.datetime = datetime;
        }

    public String gettext_inputs(String text_inputs){
        return this.logged;
    }

    public void setlogged(String logged) {
        this.logged = logged;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

}

