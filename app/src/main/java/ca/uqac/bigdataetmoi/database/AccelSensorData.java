package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by pat on 2017-11-16.
 */

public class AccelSensorData implements SensorData {
    public static final String DATA_ID = "accelsensordata";

    Date mDate;
    boolean mMoving;

    public AccelSensorData(Date date, boolean moving)
    {
        mDate = date;
        mMoving = moving;
    }

    @Override
    public String getDataID() { return DATA_ID; }
}
