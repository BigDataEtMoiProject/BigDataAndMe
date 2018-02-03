package ca.uqac.bigdataetmoi.database.data_models;

import android.location.Location;

/**
 * Created by pat on 2018-02-03.
 */

public class LocationData extends Location implements SensorData
{
    public static final String DATA_ID = "locations";

    public LocationData(Location location)
    {
        super(location);
    }

    @Override
    public String getDataID() { return DATA_ID; }
}
