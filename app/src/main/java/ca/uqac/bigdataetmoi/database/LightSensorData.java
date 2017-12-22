package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by pat on 2017-11-16.
 */

public class LightSensorData implements SensorData {
    public static final String DATA_ID = "lightsensordata";

    Date mDate;
    float mLux;

    public LightSensorData() {}

    public LightSensorData(Date date, float lux)
    {
        mDate = date;
        mLux = lux;
    }

    @Override
    public String getDataID() { return DATA_ID; }

    public Date getDate() { return mDate; }
    public float getLux() { return mLux; }

    static public String interpreterLux(float lux)
    {
        String text;

        if(lux <= 10.0)
            text = "Noir";
        else if(lux <= 30.0)
            text = "Sombre";
        else if(lux <= 125)
            text = "Faible éclairage";
        else if(lux <= 300)
            text = "Éclairage moyen";
        else if(lux <= 700)
            text = "Éclairage normal";
        else if(lux <= 7500)
            text = "Clair";
        else
            text = "Très clair";

        return text;
    }
}

