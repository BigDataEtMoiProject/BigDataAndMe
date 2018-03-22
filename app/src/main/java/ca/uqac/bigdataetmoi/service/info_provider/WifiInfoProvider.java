package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.DataCollection;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Raph on 21/11/2017.
 *
 * Modifié par Patrick lapointe le 2018-03-13
 */

public class WifiInfoProvider extends InfoProvider
{
    private WifiManager mWifiManager;
    private List<String> mWifiSSID;

    private BroadcastReceiver mBroadCastReceiver;

    public WifiInfoProvider(Context context)
    {
        mWifiSSID = new ArrayList<>();
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        mBroadCastReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                List<ScanResult> resultsWifiScan = mWifiManager.getScanResults();
                for(int i = 0 ; i < resultsWifiScan.size() ; i++)
                    mWifiSSID.add(resultsWifiScan.get(i).SSID);

                context.unregisterReceiver(this);
                DataCollection collection = new DataCollection();
                collection.wifiSSID = mWifiSSID;
                generateDataReadyEvent(collection);
            }
        };

        // On tente d'activer le wifi si ce n'est pas déjà fait
        if(!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);

        // Si le wifi est activé, on demande un scan
        if(mWifiManager.isWifiEnabled()) {
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(mBroadCastReceiver, filter);
            mWifiManager.startScan();
        }
    }
}