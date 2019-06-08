package ca.uqac.bigdataetmoi.workers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import retrofit2.Call;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.dto.WifiDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import timber.log.Timber;

public class WifiWorker extends Worker {
    public static final String OUTPUT_KEY = "user";
    private Context appContext;
    private WifiManager mWifiManager;
    private List<ScanResult> ScannedResults;
    private ArrayList<WifiDto> wifis = new ArrayList<WifiDto>();

    public WifiWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        this.appContext = appContext;

        mWifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        appContext.registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                ScannedResults = mWifiManager.getScanResults();
            }

        }
    };

    @NonNull
    @Override
    public Result doWork() {
        try {
            for(int i=0; i < ScannedResults.size(); i++) {
                ScanResult scanResult = ScannedResults.get(i);

                WifiDto wifiDto = new WifiDto("", "", "");
                wifiDto.name = "";
                wifiDto.ssid = scanResult.SSID;
                Date dateFormat = new Date();
                wifiDto.date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateFormat);

                wifis.add(wifiDto);
            }

            WifiDto[] wifiList = wifis.toArray(new WifiDto[wifis.size()]);

            Call<User> wifiCall = new HttpClient<UserService>(appContext).create(UserService.class).sendWifi(wifiList);

            wifiCall.execute();
            return Result.success();
        } catch (SecurityException e) {
            Timber.e(e);
            return Result.failure();
        } catch (Exception e) {
            Timber.e(e);
            return Result.failure();
        }
    }
}
