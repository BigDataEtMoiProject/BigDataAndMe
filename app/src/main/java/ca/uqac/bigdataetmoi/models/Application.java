package ca.uqac.bigdataetmoi.models;


public class Application  implements Comparable< Application >{
    public String appName;
    public String datetime;

    public Application(String appName, String datetime) {
        this.appName = appName;
        this.datetime = datetime;
    }

    public String getAppName(){
        return this.appName;
    }

    public String getDatetime(){
        return this.datetime;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int compareTo(Application o) {
        return this.getDatetime().compareTo(o.getDatetime());
    }

}