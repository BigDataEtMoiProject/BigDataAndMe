package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by Raph on 21/11/2017.
 */

public class BluetoothData {

    private String deviceName;
    private String address;
    private Date mDate;

    public BluetoothData(Date date, String deviceName, String address) {

        this.mDate = date;
        this.deviceName = deviceName;
        this.address = address;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }
}