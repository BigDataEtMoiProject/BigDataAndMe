package ca.uqac.bigdataetmoi.database.data_models;

import java.util.Date;

/**
 * Created by Raph on 22/11/2017.
 */

public class WifiData implements Data
{
    public static final String DATA_ID = "wifidata";

    private String deviceName;
    private Date mDate;

    public WifiData(Date date, String deviceName) {
        this.mDate = date;
        this.deviceName = deviceName;
    }

    @Override
    public String getDataID() { return DATA_ID; }

    public String getDeviceName() {
        return deviceName;
    }

}