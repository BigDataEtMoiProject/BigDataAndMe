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
    FirebaseDatabase mDb;
    DatabaseReference mDbRef;

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
        // Pour supprimer les données de la bd : mDb.getReference("users").removeValue();
    }

    // Écriture des données de sensur dans la BD
    public void storeSensorData(Data data)
    {
        String key = mDbRef.child(data.getDataID()).push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/" + data.getDataID() + "/" + key, data);
        mDbRef.updateChildren(update);
        Log.w("DatabaseManager", "New " + data.getDataID() + " stored");
    }

    // Lecture dans la bd
    public DatabaseReference getDbRef(String dataId) { return mDbRef.child(dataId); }
}

