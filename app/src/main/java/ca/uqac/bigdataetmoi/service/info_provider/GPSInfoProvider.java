package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.*;

/**
 * Created by Patrick Lapointe on 2018-03-13.
 * Récupération des données du GPS et écriture dans la base de données
 */

public class GPSInfoProvider implements LocationListener
{
    final long LOC_UPDATE_MIN_TIME = 500; //in ms
    final float LOC_UPDATE_MIN_DISTANCE = 20; //in meters

    DataReadyListener mListener;

    public GPSInfoProvider(Context context, DataReadyListener listener)
    {
        mListener = listener;

        if (PermissionManager.getInstance().isGranted(ACCESS_FINE_LOCATION)){
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, this);
            } catch (SecurityException e) {
                e.getMessage();
            }
        }
        else
            mListener.dataReady(null);
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationData data = new LocationData(null, location.getLatitude(), location.getLongitude());
        mListener.dataReady(data);
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}

}
