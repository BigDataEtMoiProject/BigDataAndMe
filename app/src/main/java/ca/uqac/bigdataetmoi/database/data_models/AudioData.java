package ca.uqac.bigdataetmoi.database.data_models;

import java.util.Date;

/**
 * Created by Job on 29/12/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class AudioData implements Data {
    public static final String DATA_ID = "SoundData";

    Date mDate;
    double mloudness;
    String interpretation="";

    public AudioData(Date date, double loudness)
    {
        mDate = date;
        mloudness = loudness;
    }

    @Override
    public String getDataID() { return DATA_ID; }

    public Date getDate() { return mDate; }
    public double getLoudness() { return mloudness; }
    public String getInterpretation()
    {
        interpreteLoudness(mloudness);
        return interpretation;
    }

    public void interpreteLoudness(double loudness)
    {
        if(loudness < 1000 )
        {
            interpretation="Little sound or no sound at all";
        }
        else if(loudness >= 1000 && loudness < 7000)
        {
            interpretation="Medium sound maybe talking or music in a low level";
        }
        else if(loudness >= 7000 && loudness < 15000)
        {
            interpretation="A little bit loud maybe talking or music in a high level";
        }
        else if(loudness >= 15000)
        {
            interpretation="Very loud maybe a concert or something";
        }
    }
}
