package ca.uqac.bigdataetmoi.database;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.bigdataetmoi.MainApplication;

/**
 * Created by pat on 2017-11-16.
 * Cette classe à pour but de gérer la base de données, c'est à dire son initialisation et la communication.
 */

public class DatabaseManager
{
    FirebaseDatabase mDb;
    DatabaseReference mDbRef;
    private static final String mUserID = "0";

    private static DatabaseManager mInstance = null;

    public static synchronized DatabaseManager getInstance()
    {
        if(mInstance == null)
            mInstance = new DatabaseManager();
        return mInstance;
    }

    private DatabaseManager()
    {
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference("users").child(MainApplication.user.getUid());
    }

    // Écriture dans la bd

    public void storeLocationData(Location loc)
    {
        String key = mDbRef.child("locations").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/locations/" + key, loc);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New location stored");
    }

    public void storeLightSensorData(LightSensorData data)
    {
        String key = mDbRef.child("lightsensordata").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/lightsensordata/" + key, data);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New lux stored");
    }

    public void storeAccelSensorData(AccelSensorData data)
    {
        String key = mDbRef.child("accelsensordata").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/accelsensordata/" + key, data);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New accel stored");
    }

    public void storeUsageData(UsageData data) {
        String key = mDbRef.child("usagedata").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/usagedata/" + key, data);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New usage data stored");
    }

    // Lecture dans la bd

    public DatabaseReference getLocationRef()
    {
        return mDb.getReference("users/" + MainApplication.user.getUid() + "/locations");
    }

    public DatabaseReference getUsageRef()
    {
        return mDb.getReference("users/" + MainApplication.user.getUid() + "usagedata");
    }
}

