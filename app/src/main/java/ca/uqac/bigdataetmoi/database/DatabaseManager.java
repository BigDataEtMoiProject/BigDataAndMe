package ca.uqac.bigdataetmoi.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.bigdataetmoi.MainApplication;
import ca.uqac.bigdataetmoi.database.data_models.Data;

/**
 * Created by Patrick Lapointe on 2017-11-16.
 * Cette classe à pour but de gérer la base de données, c'est à dire son initialisation et la communication.
 */

public class DatabaseManager
{
    private static DatabaseManager mInstance = null;
    private String mUserId;
    private DatabaseReference mDb;

    public static synchronized DatabaseManager getInstance()
    {
        if(mInstance == null)
            mInstance = new DatabaseManager();
        return mInstance;
    }

    private DatabaseManager() {
        mUserId = MainApplication.user.getUid();
        mDb = FirebaseDatabase.getInstance().getReference();
    }

    // Rétrocompatibilité avec ancienne version de la bd
    public DatabaseReference getDbRef(String dataId) { return mDb.child("users").child(MainApplication.user.getUid()).child(dataId); }

    // Écriture des données de sensur dans la BD (ancienne version)
    public void storeSensorData(Data data)  {
        String key = getDbRef(data.getDataID()).push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/" + data.getDataID() + "/" + key, data);
        mDb.child("users").child(mUserId).updateChildren(update);
        Log.w("DatabaseManager", "New " + data.getDataID() + " stored");
    }

    // Pour la version 2 de la base de données
    public DatabaseReference getDbRef() {
        return mDb;
    }
    public DatabaseReference getSensorDataDbRef() {
        return mDb.child("sensordata").child(mUserId);
    }
    public DatabaseReference getPermissionDbRef() { return mDb.child("permission").child(mUserId); }

    public void storeSensorDataCollection(SensorDataCollection collection) {
        String timestamp = Long.toString(System.currentTimeMillis());
        getSensorDataDbRef().child(timestamp).setValue(collection);
    }
}

