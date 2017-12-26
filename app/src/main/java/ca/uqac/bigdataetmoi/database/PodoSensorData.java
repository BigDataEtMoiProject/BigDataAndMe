package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by fs77 on 20/11/2017.
 */

public class PodoSensorData {
    public String getpDate() {
        return pDate;
    }

    public float getpMove() {
        return pMove;
    }

    String pDate;
    float pMove;

    public PodoSensorData()
    {
        pDate = null;
        pMove = (float) 0.0;

    }

    public PodoSensorData(String date,float move)
    {
        pDate = date;
        pMove = move;

    }
}
