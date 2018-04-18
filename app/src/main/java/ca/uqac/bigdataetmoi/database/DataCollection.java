package ca.uqac.bigdataetmoi.database;

import android.location.Location;

import java.util.List;

/**
 * Created by Patrick Lapointe on 2018-02-27.
 * But : Stocker les données des senseurs selon la version 2 de la bd.
 * Le but étant de regrouper l'information de tous les capteurs sous une même classe.
 */

public class DataCollection {
    public Boolean isMoving;
    public Float luxLevel;
    public Float proximityDistance;
    public Double latitude;
    public Double longitude;
    public List<String> wifiSSID;
    public List<String> bluetoothSSID;
    public Float steps;
    public Double soundLevel;
    public Boolean screenState;

    public DataCollection() {}

    // Détermine si les données on tous été recues
    public boolean allDataReceived() {

        if(isMoving != null && luxLevel != null && proximityDistance != null
                && latitude != null && longitude != null && wifiSSID != null && bluetoothSSID != null
                && steps != null && soundLevel != null && screenState != null)
            return true;
        else
            return false;
    }

    // Transfert les données non-nuls de l'objet DataCollection en paramètre
    public void receiveData(DataCollection collection) {
        if(collection.isMoving != null)
            isMoving = collection.isMoving;
        if(collection.luxLevel != null)
            luxLevel = collection.luxLevel;
        if(collection.proximityDistance != null)
            proximityDistance = collection.proximityDistance;
        if(collection.latitude != null)
            latitude = collection.latitude;
        if(collection.longitude != null)
            longitude = collection.longitude;
        if(collection.wifiSSID != null)
            wifiSSID = collection.wifiSSID;
        if(collection.bluetoothSSID != null)
            bluetoothSSID = collection.bluetoothSSID;
        if(collection.steps != null)
            steps = collection.steps;
        if(collection.soundLevel != null)
            soundLevel = collection.soundLevel;
        if(collection.screenState != null)
            screenState = collection.screenState;
    }
}
