package ca.uqac.bigdataetmoi.database.data_models;

import java.util.Date;

/**
 * Created by pat on 2017-11-16.
 */

public class AccelSensorData implements Data {
    public static final String DATA_ID = "accelsensordata";

    Date mDate;
    boolean mMoving;

    public AccelSensorData() { }

    public AccelSensorData(Date date, boolean moving)
    {
        mDate = date;
        mMoving = moving;
    }

    @Override
    public String getDataID() { return DATA_ID; }

    public Date getDate() { return mDate; }
    public boolean isMoving() { return mMoving; }
}
