package ca.uqac.bigdataetmoi.workers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import ca.uqac.bigdataetmoi.events.OnPhotoUploadedEvent;
import ca.uqac.bigdataetmoi.events.OnWifiUploadedEvent;
import retrofit2.Call;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.dto.WifiDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import retrofit2.Response;
import timber.log.Timber;

public class WifiWorker extends Worker {
    private Context appContext;
    private WifiManager mWifiManager;
    private List<ScanResult> ScannedResults;
    private ArrayList<WifiDto> wifis = new ArrayList<WifiDto>();

    public WifiWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        appContext.registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager = (WifiManager) appContext.getSystemService(appContext.WIFI_SERVICE);
        mWifiManager.startScan();

        this.appContext = appContext;
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
        mWifiManager.startScan();
        ScannedResults = mWifiManager.getScanResults();
        try {
            Timber.d("I'M HERE§");
            for(int i = 0; i < ScannedResults.size(); i++) {
                ScanResult scanResult = ScannedResults.get(i);

                WifiDto wifiDto = new WifiDto("", "", "");
                wifiDto.name = scanResult.capabilities;
                wifiDto.ssid = scanResult.SSID;
                Date dateFormat = new Date();
                wifiDto.date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateFormat);

                wifis.add(wifiDto);
            }

            WifiDto[] wifiList = wifis.toArray(new WifiDto[wifis.size()]);

            Call<User> wifiCall = new HttpClient<UserService>(appContext).create(UserService.class).sendWifi(wifiList);

            try {
                Response<User> res = wifiCall.execute();
                if (res.isSuccessful() && res.body() != null) {
                    EventBus.getDefault().post(new OnWifiUploadedEvent(res.body()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
