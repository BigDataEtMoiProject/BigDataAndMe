package ca.uqac.bigdataetmoi.activity.data_usage_activity;

import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.UsageData;

public class DonneesUtilisationActivity extends AppCompatActivity {

    private static final long INIT_TIME_PERIOD = 1000 * 30;     //30 secondes
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    private static String TAG = "Event";

    private long mAppStarted, mAppEnded, mPrevUsageTime;
    private String mAppInForeground, mAppInBackground;
    String[] choices;
    Context mContext;

    //Variable for database management
    DatabaseManager dbManager;
    DatabaseReference usageRef;
    UsageStatsManager statsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);
        setLayoutAndData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_usage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.stats_picker) {
            getStatPicker();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        readFirebaseDatabase();
    }

    public void setLayoutAndData() {

        mContext = getApplicationContext();

        setTitle("Phone Usage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppStarted = mAppEnded = 0;
        mAppInForeground = mAppInBackground = "";
        mPrevUsageTime = 0;

        statsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();
    }

    public void getStatPicker() {

        choices = new String [] {"Timeline", "Details"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose type of stats")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      switch (which) {
                          case 0:
                              startActivity(new Intent(DonneesUtilisationActivity.this, UsageTimelineActivity.class));
                              break;
                          case 1:
                              startActivity(new Intent(DonneesUtilisationActivity.this, UsageAppActivity.class));
                              break;
                      }
                    }
                });
        builder.create();
        builder.show();
    }

    public final void readFirebaseDatabase() {

        usageRef.limitToLast(1).orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    UsageData usage  = getLastDataInDatabase(dataSnapshot);
                    UsageEvents events = getTimeRangeEvent(usage);
                    updateUsageDatabase(events);
                }
                else if (!dataSnapshot.exists())
                {
                    UsageData usage  = new UsageData("", 0, 0);
                    UsageEvents events = getTimeRangeEvent(usage);
                    updateUsageDatabase(events);
                }
                else
                {
                    Log.e("ERROR", "SOMETHING WENT WRONG");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Get last time an event was saved in database
    private UsageData getLastDataInDatabase(DataSnapshot ds) {

        UsageData usage = new UsageData();

        for (DataSnapshot usageSnapshot : ds.getChildren()) {
            long timeAppEnded = (long) usageSnapshot.child("timeAppEnd").getValue();
            usage.setTimeAppEnd(timeAppEnded);
        }

        return usage;
    }

    //Get the time range of events from last usage and current time
    public final UsageEvents getTimeRangeEvent(UsageData usage) {

        long currentTime = System.currentTimeMillis();
        long lastTime = 0;

        if (usage.getTimeAppEnd() == 0) {
            lastTime = currentTime - INIT_TIME_PERIOD;
        } else {
            lastTime = usage.getTimeAppEnd();
        }

        UsageEvents events;
        events = statsManager.queryEvents(lastTime, currentTime);
        return events;
    }

    //Update database with by getting when an app is in foreground and background
    public final void updateUsageDatabase(UsageEvents events) {

        while (events.hasNextEvent())
        {
            checkUsageEvents(events);
            compareEventsAndInsert();
        }
    }

    //Check if an event is in foreground and background
    public final void checkUsageEvents(UsageEvents events) {

        UsageEvents.Event event = new UsageEvents.Event();
        events.getNextEvent(event);

        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)            //Get detail when an app is open (in foreground)
        {
            mAppInForeground = event.getPackageName();
            mAppStarted = event.getTimeStamp();

            if (!events.hasNextEvent()) {                               //Assure to get the correct timestamp when no next event.
                mPrevUsageTime = mAppStarted;                             //Useful when using refreshing and get using the app name condition
            }
        }
        else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND)    //Get detail when an app is closed (in background)
        {
            mAppInBackground = event.getPackageName();
            mAppEnded = event.getTimeStamp();

            if (!events.hasNextEvent()) {
                mPrevUsageTime = mAppEnded;
            }
        }

    }

    //Check if the two last events matches and insert in database if so
    public final void compareEventsAndInsert() {

        if (mAppInForeground.equals(mAppInBackground))                    //Assure data updated correspond to the same app
        {
            if (mAppEnded != 0 && mAppStarted != 0)                       //Assure data updated is not null
            {
                long diff = mAppEnded - mAppStarted;

                if (diff >= 1000) {                                     //Filter information to remove process events (and last than 1 seconde)
                    Log.d(TAG, mAppInForeground + "\t" + "Started at\t" + dateFormat.format(mAppStarted));
                    Log.d(TAG, mAppInBackground + "\t" + "Ended at\t" + dateFormat.format(mAppEnded));
                    Log.d(TAG, mAppInForeground + ":\t" + diff / 1000 + " secondes");

                    UsageData usage = new UsageData();
                    usage.setPackageName(mAppInForeground);
                    usage.setTimeAppBegin(mAppStarted);
                    usage.setTimeAppEnd(mAppEnded);
                    dbManager.storeUsageData(usage);

                    //Put variable at 0 to assure data a clean
                    mAppStarted = mAppEnded = 0;
                    mAppInForeground = mAppInBackground = "";
                }
            }
        }
    }
}
