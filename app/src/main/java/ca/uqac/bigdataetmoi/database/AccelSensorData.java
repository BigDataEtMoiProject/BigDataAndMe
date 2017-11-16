package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by pat on 2017-11-16.
 */

public class AccelSensorData {
    Date mDate;
    boolean mMoving;

    public AccelSensorData(Date date, boolean moving)
    {
        mDate = date;
        mMoving = moving;

    }
}
