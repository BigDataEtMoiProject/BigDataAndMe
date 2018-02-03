package ca.uqac.bigdataetmoi.service;

import android.app.IntentService;

import android.app.Service;

import android.content.Context;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.location.Location;

import android.location.LocationListener;

import android.location.LocationManager;

import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.IBinder;

import android.support.v4.content.ContextCompat;

import android.util.Log;

import android.widget.Toast;



import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.data_models.LocationData;
import ca.uqac.bigdataetmoi.service.threads.BasicSensorThread;
import ca.uqac.bigdataetmoi.service.threads.BluetoothThread;
import ca.uqac.bigdataetmoi.service.threads.MicroThread;
import ca.uqac.bigdataetmoi.service.threads.PodoSensorThread;
import ca.uqac.bigdataetmoi.service.threads.WifiThread;

import static ca.uqac.bigdataetmoi.MainActivity.BTAdapter;

/*
Créé le 2017-11-16 par Patrick Lapointe
But : Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
*/

public class BigDataService extends IntentService
{
    final int LOC_UPDATE_MIN_TIME = 10000; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec

    Thread mBasicSensorThread;
    Thread MicThread;
    Thread PodoSensorThread;

    public static DatabaseManager dbManager;

    public BigDataService()
    {
        super("BigDataService");
    }

    @Override
    public void onCreate()
    {
        dbManager = DatabaseManager.getInstance();

        // Activer la wifi si elle est désactiver
        WifiThread.wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
        if (WifiThread.wifi.isWifiEnabled() == false)
        {
            WifiThread.wifi.setWifiEnabled(true);
        }

        Log.v("BigDataService", "BigDataService service has been created");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.w("BigDataService", "BigDataService service has started");

        // Démarrage du Thread pour le Micro
        MicThread = new Thread(new MicroThread(this));
        MicThread.start();

        //Démarrage du Thread pour le podometre
        PodoSensorThread = new Thread(new PodoSensorThread(this));
        PodoSensorThread.start();

        // Démarrage du Thread pour les senseurs de base
        mBasicSensorThread = new Thread(new BasicSensorThread(this));
        mBasicSensorThread.start();

        // Pour le bluetooth
        if(BTAdapter != null) {
            Thread t = new Thread(new BluetoothThread(this));
            t.start();
        }

        // Pour le wifi
        Thread tW = new Thread(new WifiThread(this));
        tW.start();

        // Pour le GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dbManager.storeSensorData(new LocationData(location));
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        boolean accessCoarseLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean accessFineLocation = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        if (accessCoarseLocation && accessFineLocation)
        {
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
        unregisterReceiver(BluetoothThread.bReciever);
        unregisterReceiver(WifiThread.bRecieverW);
        Log.i("BigDataService", "Service onDestroy");
    }

    protected void onHandleIntent(Intent intent) {
        Log.i("BigDataService", "Service onHandleIntent");
    }
}