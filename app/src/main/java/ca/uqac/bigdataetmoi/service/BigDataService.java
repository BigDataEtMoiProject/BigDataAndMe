package ca.uqac.bigdataetmoi.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import ca.uqac.bigdataetmoi.database.DatabaseManager;



/*
Créé le 2017-11-16 par Patrick Lapointe
But : Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
 */


public class BigDataService extends IntentService {
    final int LOC_UPDATE_MIN_TIME = 10000; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec

    Thread mBasicSensorThread;
    Thread MicThread;

    DatabaseManager dbManager;

    public BigDataService() {
        super("BigDataService");
    }

    @Override
    public void onCreate() {

        dbManager = DatabaseManager.getInstance();
        Log.v("BigDataService", "BigDataService service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("BigDataService", "BigDataService service has started");

        // Démarrage du Thread pour le Micro
        MicThread = new Thread(new MicroThread(this));
        MicThread.start();

        // Démarrage du Thread pour les senseurs de base
        mBasicSensorThread = new Thread(new BasicSensorThread(this));
        mBasicSensorThread.start();

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
<<<<<<< HEAD


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

    //Gestion des événements d'utilisation.

    public final void onUsageChanged(UsageStatsManager statsManager) {
        long lastTime, currentTime;
        long appStarted, appEnded;
        String appInForeground, appInBackground;
        Date timeAppStart, timeAppEnd;

        //Initialize variables
        appStarted = appEnded = 0;
        appInForeground = appInBackground = new String();
        currentTime = System.currentTimeMillis();

        if (mPrevUsageTime == 0) {      //TODO: get last data saved, and start monitering from there.
            lastTime = currentTime - REFRESH_TIME;  //Initiate the monitering app usage
        } else {
            lastTime = mPrevUsageTime;              //Get last timestamp saved last iteration to a save data correctly.
        }

        UsageEvents events = statsManager.queryEvents(lastTime, currentTime);   //get range of event between lastTime and currentTime

        while (events.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);

            if (event.getEventType() == event.MOVE_TO_FOREGROUND)        //When an app is launched
            {
                appInForeground = event.getPackageName();
                appStarted = event.getTimeStamp();

                Log.d("Event", appInForeground + "\t" + "Started at" + dateFormat.format(appStarted));
                if (!events.hasNextEvent()) {
                    mPrevUsageTime = appStarted;
                }
            } else if (event.getEventType() == event.MOVE_TO_BACKGROUND)  //When an app is closed
            {
                appInBackground = event.getPackageName();       //get packagename event when app is closed
                appEnded = event.getTimeStamp();                //get timestamp event when app is closed

                Log.d("Event", appInBackground + "\t" + "Ended at" + dateFormat.format(appEnded));
                if (!events.hasNextEvent()) {       //Check if there is a next event
                    mPrevUsageTime = appEnded;     //If not, assign last time stamp to mPrevUsageTime (last value used each time it refresh)
                }
            }

            if (appInForeground.equals(appInBackground)) {      //name the app that starts is the same as the app that ends
                if(appEnded != 0 && appStarted != 0) {          //make sure there is data
                    long diff = (appEnded - appStarted);

                    timeAppStart = new Date(appStarted);        //transform long in Date
                    timeAppEnd = new Date(appEnded);

                    Log.d("Event", appInForeground + "\t" + "\t" + "Time spent " + diff / 1000 + "secondes");
                    //Send data in firebase database (see DatabaseManager)
                    UsageData usage = new UsageData();
                    usage.setPackageName(appInForeground);
                    usage.setTimeAppBegin(timeAppStart);
                    usage.setTimeAppEnd(timeAppEnd);
                    dbManager.storeUsageData(usage);

                    //Put variable at 0 to assure data a clean
                    appStarted = appEnded = 0;
                    appInForeground = appInBackground = "";
                }
            }
        }
    }
=======
>>>>>>> 3c667fbe63533c166b34dd65009d67c693cac72d
}