package ca.uqac.bigdataetmoi.service;

import android.app.IntentService;
import android.app.Service;
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

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import ca.uqac.bigdataetmoi.database.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.LightSensorData;
import ca.uqac.bigdataetmoi.database.ProximitySensorData;


/*
Créé le 2017-11-16 par Patrick Lapointe
But : Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
 */


public class BigDataService extends IntentService implements SensorEventListener {
    final int LOC_UPDATE_MIN_TIME = 10000; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec
    final int SENSOR_MIN_UPDATE_MILLIS = 300000; //Temps minimum entre deux écriture dans la BD (5 minutes)

    DatabaseManager dbManager;
    DatabaseReference usageRef;

    private long mPrevAccelMillis, mPrevMoveMillis, mPrevProximityMillis;
    private float mAccel, mAccelCurrent, mAccelLast;

    public BigDataService() {
        super("BigDataService");
    }

    @Override
    public void onCreate() {

        mPrevMoveMillis = 0;
        mPrevAccelMillis = 0;
        mPrevProximityMillis = 0;

        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();

        Log.v("BigDataService", "BigDataService service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("BigDataService", "BigDataService service has started");

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour l'accéléromètre
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Pour le capteur de proximité
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

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


    // Gestion des événements des senseurs

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            handleLightSensorChanged(event.values[0]);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = event.values.clone();
            handleAccelSensorChanged(gravity[0], gravity[1], gravity[2]);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            handleProximitySensorChanged(event.values[0]);
        }
    }

    // Gestion des changements de valeurs des capteurs

    private void handleLightSensorChanged(float newLux)
    {
        long currMillis = System.currentTimeMillis();

        if(currMillis - mPrevAccelMillis > SENSOR_MIN_UPDATE_MILLIS) {
            dbManager.storeSensorData(new LightSensorData(Calendar.getInstance().getTime(), newLux));
            mPrevAccelMillis = currMillis;
        }
    }

    private void handleAccelSensorChanged(float accelX, float accelY, float accelZ)
    {
        // Détection d'un changement d'accélération (cela veut dire que le téléphone bouge)
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(accelX*accelX + accelY*accelY + accelZ*accelZ);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if(mAccel > 1.0) { // Cela veut dire qu'on bouge
            long currMillis = System.currentTimeMillis();

            //On met à jour les mouvement si celui-ci a lieu au minimum 5 minutes plus tard
            if(currMillis - mPrevMoveMillis > SENSOR_MIN_UPDATE_MILLIS){
                dbManager.storeSensorData(new AccelSensorData(Calendar.getInstance().getTime(), true));
                mPrevMoveMillis = currMillis;
            }
        }
    }

    private void handleProximitySensorChanged(float newDistance)
    {
        long currMillis = System.currentTimeMillis();

        if(currMillis - mPrevProximityMillis > SENSOR_MIN_UPDATE_MILLIS){
            dbManager.storeSensorData(new ProximitySensorData(Calendar.getInstance().getTime(), newDistance));
            mPrevProximityMillis = currMillis;
        }
    }
}