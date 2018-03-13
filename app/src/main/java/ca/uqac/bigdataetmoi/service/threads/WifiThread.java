package ca.uqac.bigdataetmoi.service.threads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data_models.WifiData;

/**
 * Created by Raph on 21/11/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class WifiThread extends Thread implements Runnable
{
    private Context context;
    boolean scan = false;

    // Données et broadcastReceiver pour la wifi
    public static WifiManager wifi;
    private static List<ScanResult> resultsWifiScan;
    private static int size = 0;
    private static ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

    public static BroadcastReceiver bRecieverW = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.err.println("On est rentré dans le onReceive de W ! Action = " + action);
            resultsWifiScan = wifi.getScanResults();
            size = resultsWifiScan.size();

            try
            {
                size = size - 1;
                while (size >= 0)
                {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("key", resultsWifiScan.get(size).SSID + "  " + resultsWifiScan.get(size).capabilities);

                    WifiData wifiEntry = new WifiData(Calendar.getInstance().getTime(), resultsWifiScan.get(size).SSID);
                    ca.uqac.bigdataetmoi.service.BigDataService.dbManager.storeSensorData(wifiEntry);

                    System.out.println("!!!!!!!!!!!!!POINT WIFI : " + resultsWifiScan.get(size).SSID) ;
                    arraylist.add(item);
                    size--;
                }
            }
            catch (Exception e)
            { }
        }
    };

    public WifiThread(Context context)
    {
        this.context = context;
    }

    @Override
    public void run() {
        do {
            System.out.println("JE SUIS VIVANT (WIFI) ! ");

            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(bRecieverW, filter);
            System.out.println("Starting WIFI discovery ...");
            scan = wifi.startScan();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while(scan);
    }
}