package ca.uqac.bigdataetmoi.database;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pat on 2017-11-16.
 * Cette classe à pour but de gérer la base de données, c'est à dire son initialisation et la communication.
 */

public class DatabaseManager
{
    FirebaseDatabase mDb;
    DatabaseReference mDbRef;
    private static final String mUserID = "1";

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
        mDbRef = mDb.getReference("users").child(mUserID);
    }

    // Écriture dans la bd

    public void storeLocationData(Location loc)
    {
        String key = mDb.getReference("users").child(mUserID).child("locations").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/locations/" + key, loc);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New location stored");
    }

    public void storeSensorData(SensorData data)
    {
        String key = mDb.getReference("users").child(mUserID).child(data.getDataID()).push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/" + data.getDataID() + "/" + key, data);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New " + data.getDataID() + " stored");
    }

    // Lecture dans la bd
    public DatabaseReference getLocationRef()
    {
        return mDb.getReference("users/" + mUserID + "/locations");
    }

}

