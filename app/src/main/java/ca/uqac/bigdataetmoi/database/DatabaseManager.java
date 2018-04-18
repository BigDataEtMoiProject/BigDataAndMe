package ca.uqac.bigdataetmoi.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        mUserId = ActivityFetcherActivity.getUserId();
        mDb = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDbRef() {
        return mDb;
    }
    public DatabaseReference getSensorDataDbRef() {
        return mDb.child("sensordata").child(mUserId);
    }
    public DatabaseReference getPermissionDbRef() { return mDb.child("permission").child(mUserId); }
    public DatabaseReference getCalculationDbRef() { return mDb.child("calculationdata").child(mUserId); }

    public void storeSensorDataCollection(DataCollection collection) {
        String timestamp = Long.toString(System.currentTimeMillis());
        getSensorDataDbRef().child(timestamp).setValue(collection);
    }

    public void storeSommeilCalculationData(int sleepTime) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy MM dd");
        getCalculationDbRef().child(formatter.format(new Date())).setValue(sleepTime);
    }
}

