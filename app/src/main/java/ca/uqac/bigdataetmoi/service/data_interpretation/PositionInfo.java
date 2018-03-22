package ca.uqac.bigdataetmoi.service.data_interpretation;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.service.info_provider.BluetoothInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.GPSInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.InfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.WifiInfoProvider;

/**
 * Created by sebastien on 21/03/2018.
 * Récuperation des données dans la base de donnée afin d'approximer au mieux la position de l'utilisateur
 */


public class PositionInfo extends InfoProvider implements LocationListener{

    private GPSInfoProvider mGPSInfoProvider;
    private WifiInfoProvider mWifiInfoProvider;
    private BluetoothInfoProvider mBluetoothInfoProvider;

    /*recuperation des données dans la base
    latitude, longitude, ssid wifi, ssid bluetooth
    */

    @Override
    public void onLocationChanged(Location location) {
        DataCollection collection = new DataCollection();
        //mGPSInfoProvider = collection.location.getLatitude();
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
}
