package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by Raph on 22/11/2017.
 */

public class WifiData {

    private String deviceName;
    private Date mDate;

    public WifiData(Date date, String deviceName) {

        this.mDate = date;
        this.deviceName = deviceName;

    }

    public String getDeviceName() {
        return deviceName;
    }

}