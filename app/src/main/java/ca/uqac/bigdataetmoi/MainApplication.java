package ca.uqac.bigdataetmoi;

import android.app.Application;


public class MainApplication extends Application {

    //global var that stores userID
    private String userID;
    public String getUserId(){return userID;}
    public void setUserID(String newuid){userID = newuid;}
}
