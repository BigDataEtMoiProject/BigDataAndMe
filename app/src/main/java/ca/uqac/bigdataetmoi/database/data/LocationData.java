package ca.uqac.bigdataetmoi.database.data;

import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class LocationData extends AbstractDataManager {
    private Double latitude = null, longitude = null;
    private final String key_lat = "latitude";
    private final String key_long = "longitude";
    private Boolean complete = false;

    public LocationData(DataReadyListener listener)
    {
        super.addDataReadyListener(listener);
        getLatitude();
        getLongitude();
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    private void checkComplete()
    {
        if(latitude != null && longitude != null)
            complete = true;
    }

    public Double getLatitude() {
        if(latitude != null)
            return latitude;

        super.readDataByKey(DataPath.SENSOR_DATA, "1522332541058/location/", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot location = dataSnapshot.child(key_lat);
                latitude = (Double) location.getValue();
                setLatitude(latitude);
                checkComplete();
                dataReady(complete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latitude = 0.0;
            }
        });

        return latitude;
    }

    public Double getLongitude() {
        if(longitude != null)
            return longitude;

        super.readDataByKey(DataPath.SENSOR_DATA, "1522332541058/location/", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot location = dataSnapshot.child(key_long);
                longitude = (Double) location.getValue();
                setLatitude(longitude);
                checkComplete();
                dataReady(complete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                longitude = null;
            }
        });

        return longitude;
    }

    public Boolean isComplete() {
        return complete;
    }
}
