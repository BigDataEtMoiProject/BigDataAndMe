package ca.uqac.bigdataetmoi.service;

import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.uqac.bigdataetmoi.database.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.LightSensorData;
import ca.uqac.bigdataetmoi.database.UsageData;


/*
Créé le 2017-11-16 par Patrick Lapointe
But : Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
 */


public class BigDataService extends IntentService implements SensorEventListener {
    final int LOC_UPDATE_MIN_TIME = 10000; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec
    private static final long REFRESH_TIME = 1000 * 30;     //30 secondes
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    private static String TAG = "Event";

    DatabaseManager dbManager;
    DatabaseReference usageRef;
    UsageStatsManager statsManager;
    UsageData prevUsageData;

    private long mPrevAccelMillis, mPrevMoveMillis;
    private float mAccel, mAccelCurrent, mAccelLast;
    private long prevUsageTime;

    public BigDataService() {
        super("BigDataService");
    }

    @Override
    public void onCreate() {

        mPrevMoveMillis = 0;
        mPrevAccelMillis = 0;
        prevUsageTime = 0;
        prevUsageData = new UsageData();


        statsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();

        Log.v("BigDataService", "BigDataService service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("BigDataService", "BigDataService service has started");

        // TODO: Thread séparé
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour l'accéléromètre
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Pour le GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dbManager.storeLocationData(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        //  Pour les données d'utilisations
        readAndUpdateUsageDatabase();


        boolean accessCoarseLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean accessFineLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (accessCoarseLocation && accessFineLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, locationListener);
        } else {
            Toast.makeText(getBaseContext(), "Permission to use location : denied", Toast.LENGTH_SHORT).show();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("BigDataService", "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("BigDataService", "Service onDestroy");
    }

    protected void onHandleIntent(Intent intent) {
        Log.i("BigDataService", "Service onHandleIntent");
    }


    // Gestion des événements des senseurs

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];

            // On met à jour la bd au 5 minutes pour ce capteur
            long currMillis = System.currentTimeMillis();

            if (currMillis - mPrevAccelMillis > 300000) {
                dbManager.storeLightSensorData(new LightSensorData(Calendar.getInstance().getTime(), lux));
                mPrevAccelMillis = currMillis;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Formule pour déterminer si le téléphone est en mouvement ou non.
            float[] gravity = event.values.clone();
            float x = gravity[0];
            float y = gravity[1];
            float z = gravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 1.0) { // Cela veut dire qu'on bouge
                long currMillis = System.currentTimeMillis();

                //On met à jour les mouvement si celui-ci a lieu au minimum 5 minutes plus tard
                if (currMillis - mPrevMoveMillis > 300000) {
                    dbManager.storeAccelSensorData(new AccelSensorData(Calendar.getInstance().getTime(), true));
                    mPrevMoveMillis = currMillis;
                }

            }

        }
    }

    //Gestion des événements d'utilisation. //TODO READ WHEN PHONE IS CLOSED
    public final void readAndUpdateUsageDatabase() {
        usageRef.limitToLast(1).orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    UsageData usage = new UsageData();

                    //GET LAST REGISTER DATA IN FIREBASE
                    for (DataSnapshot usageSnapshot : dataSnapshot.getChildren()) {
                        long timeAppEnded = (long) usageSnapshot.child("timeAppEnd").child("time").getValue();
                        usage.setTimeAppEnd(new Date(timeAppEnded));
                    }
                    //UPDATE DATA SINCE LAST PHONE USAGE REGISTERED
                    Log.d("EVENT", "Last usage time:\t" + usage.getTimeAppEnd());
                    long currentTime = System.currentTimeMillis();
                    long lastTime = usage.getTimeAppEnd().getTime();
                    UsageEvents events = getTimeRangeEvent(lastTime, currentTime);
                    updateUsageDatabase(events);
                } else if (!dataSnapshot.exists()) {
                    //INSERT FIRST DATA IN USER DATABASE EMPTY
                    Log.d("ERROR", "NO DATA AVAILABLE, INSERT DATA");
                    long currentTime = System.currentTimeMillis();
                    long lastTime = currentTime - REFRESH_TIME;
                    UsageEvents events = getTimeRangeEvent(lastTime, currentTime);
                    updateUsageDatabase(events);
                } else {
                    Log.e("ERROR", "SOMETHING WENT WRONG");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Get the time range of events from last usage and current time
    public final UsageEvents getTimeRangeEvent(long lastTime, long currentTime) {
        UsageEvents events;
        events = statsManager.queryEvents(lastTime, currentTime);
        return events;
    }

    //Update database with by getting when an app is in foreground and background
    public final void updateUsageDatabase(UsageEvents events) {
        long appStarted, appEnded;
        String appInForeground, appInBackground;

        appStarted = appEnded = 0;
        appInForeground = appInBackground = "";

        while (events.hasNextEvent())
        {
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);

            if (event.getEventType() == event.MOVE_TO_FOREGROUND)            //Get detail when an app is open (in foreground)
            {
                appInForeground = event.getPackageName();
                appStarted = event.getTimeStamp();

                if (!events.hasNextEvent()) {                               //Assure to get the correct timestamp when no next event.
                    prevUsageTime = appStarted;                             //Useful when using refreshing and get using the app name condition
                }
            }
            else if (event.getEventType() == event.MOVE_TO_BACKGROUND)    //Get detail when an app is closed (in background)
            {
                appInBackground = event.getPackageName();
                appEnded = event.getTimeStamp();

                if (!events.hasNextEvent()) {
                    prevUsageTime = appEnded;
                }
            }

            if (appInForeground.equals(appInBackground))                    //Assure data updated correspond to the same app
            {
                if (appEnded != 0 && appStarted != 0)                       //Assure data updated is not null
                {
                    long diff = appEnded - appStarted;

                    if (diff >= 1000) {                                     //Filter information to remove process events (and last than 1 seconde)
                        Log.d(TAG, appInForeground + "\t" + "Started at\t" + dateFormat.format(appStarted));
                        Log.d(TAG, appInBackground + "\t" + "Ended at\t" + dateFormat.format(appEnded));
                        Log.d(TAG, appInForeground + ":\t" + diff / 1000 + " secondes");

                        UsageData usage = new UsageData();
                        usage.setPackageName(appInForeground);
                        usage.setTimeAppBegin(new Date(appStarted));
                        usage.setTimeAppEnd(new Date(appEnded));
                        //dbManager.storeUsageData(usage);

                        //Put variable at 0 to assure data a clean
                        appStarted = appEnded = 0;
                        appInForeground = appInBackground = "";
                    }
                }
            }
        }
    }

    public final void getLastEvent(UsageData lastUsage, DataSnapshot dataSnapshot)
    {
        for (DataSnapshot usageSnapshot : dataSnapshot.getChildren())
        {
            long timeAppEnded = (long) usageSnapshot.child("timeAppEnd").child("time").getValue();
            lastUsage.setTimeAppEnd(new Date(timeAppEnded));
        }
    }
}