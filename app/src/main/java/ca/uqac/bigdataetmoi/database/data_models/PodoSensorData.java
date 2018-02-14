package ca.uqac.bigdataetmoi.database.data_models;

/**
 * Created by fs77 on 20/11/2017.
 */

public class PodoSensorData implements Data
{
    public static final String DATA_ID = "podosensordata";

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

    @Override
    public String getDataID() { return DATA_ID; }

    public String getpDate() {
        return pDate;
    }
    public float getpMove() {
        return pMove;
    }
}
