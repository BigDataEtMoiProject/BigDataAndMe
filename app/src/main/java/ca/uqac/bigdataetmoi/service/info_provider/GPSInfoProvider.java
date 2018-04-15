package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.*;

/**
 * Created by Patrick Lapointe on 2018-03-13.
 */

public class GPSInfoProvider extends InfoProvider implements LocationListener
{
    final int LOC_UPDATE_MIN_TIME = 0; //in ms
    final int LOC_UPDATE_MIN_DISTANCE = 0; //in sec

    private LocationManager mLocationManager;

    public GPSInfoProvider(Context context)
    {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (PermissionManager.getInstance().isGranted(ACCESS_FINE_LOCATION)){
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, this);
            } catch (SecurityException e) {
                e.getMessage();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationManager.removeUpdates(this);
        DataCollection collection = new DataCollection();
        collection.latitude = location.getLatitude();
        collection.longitude = location.getLongitude();
        generateDataReadyEvent(collection);
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}

}
