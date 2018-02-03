package ca.uqac.bigdataetmoi.database.data_models;

import java.util.Date;

/**
 * Created by pat on 2017-11-27.
 */

public class ProximitySensorData implements SensorData {
    public static final String DATA_ID = "proximitysensordata";

    Date mDate;
    float mDistance;

    public ProximitySensorData() {}

    public ProximitySensorData(Date date, float distance)    {
        mDate = date;
        mDistance = distance;
    }

    @Override
    public String getDataID() { return DATA_ID; }

    public Date getDate() { return mDate; }
    public float getDistance() { return mDistance; }
}
