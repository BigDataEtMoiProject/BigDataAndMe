package ca.uqac.bigdataetmoi.models;

import java.util.Collections;
import java.util.List;

public class User {
    public String id = "undefined";
    public String email = "undefined";
    public String password = "undefined";
    public List<Wifi> wifiList = Collections.emptyList();
    public List<Message> messageList = Collections.emptyList();
    public List<TimeOnApp> timeOnAppList = Collections.emptyList();
}