package ca.uqac.bigdataetmoi;

import android.*;
import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class ServiceGPS extends IntentService
{
    FirebaseDatabase db;
    DatabaseReference dbRef;
    String userID;

    final int LOC_UPDATE_MIN_TIME = 10000; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec


    public ServiceGPS()
    {
        super("ServiceGPS");
    }
    @Override
    public void onCreate()
    {
        Log.v("GPSService", "GPS service has been created");

        db = FirebaseDatabase.getInstance();
        userID = ((MainApplication) this.getApplication()).getUserId();
        dbRef = db.getReference("users").child(userID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.w("GPSService", "GPS service has started");

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor gpsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { storeLocationData(location); }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        boolean accessCoarseLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean accessFineLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if(accessCoarseLocation && accessFineLocation){
//            Toast.makeText(getBaseContext(), "Permission to use location : granted", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, locationListener);
        } else {
            Toast.makeText(getBaseContext(), "Permission to use location : denied", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    private void storeLocationData(Location loc)
    {
        String key = db.getReference("users").child(userID).child("locations").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/locations/" + key, loc);
        dbRef.updateChildren(update);
//        Toast.makeText(getBaseContext(), "GPS Service : new location stored", Toast.LENGTH_SHORT).show();
        Log.w("GPSService", "New location stored");
    }

    @Override
    public void onDestroy()
    {
        Log.v("KILL_SERVICE", "GPS service has been destroyed");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.v("SERVICE_BINDING", "GPS service onBind has been called");
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Toast.makeText(getBaseContext(), "GPS service : onHandleIntent", Toast.LENGTH_SHORT).show();
    }
}
