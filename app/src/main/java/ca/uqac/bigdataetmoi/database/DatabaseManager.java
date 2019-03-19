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
 *
 * ATTENTION : CECI NE DEVRAIT PLUS ÊTRE UTILISÉ, IL FAUT PASSER PAR LE ABSTRACTDATAMANAGER À LA PLACE.
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

    public DatabaseReference getPermissionDbRef() { return mDb.child("permission").child(mUserId); }
}

