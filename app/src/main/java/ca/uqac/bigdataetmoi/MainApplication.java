package ca.uqac.bigdataetmoi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

import ca.uqac.bigdataetmoi.activity.BaseActivity;


public class MainApplication extends Application
{
    private static MainApplication mInstance;
    private static BaseActivity mCurrentActivity = null;

    //global var that stores userID
    private String userID;
    public String getUserId(){return userID;}
    public void setUserID(String newuid){userID = newuid;}

    public static FirebaseUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }
    public static BaseActivity getCurrentActivity() { return mCurrentActivity; }
    public static void setCurrentActivity(BaseActivity activity) { mCurrentActivity = activity; }
}
