package ca.uqac.bigdataetmoi.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import ca.uqac.bigdataetmoi.database.data.BluetoothData;
import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.database.data.WifiData;
import ca.uqac.bigdataetmoi.service.info_provider.BluetoothInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;
import ca.uqac.bigdataetmoi.service.info_provider.GPSInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.NoiseInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.WifiInfoProvider;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

/**
* Créé le 2017-11-16 par Patrick Lapointe
* Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
*/

public class BigDataService extends Service
{
    private final static int REPETITION_DELAY = 1; // Mise à jour des données au x minutes

    Context mContext;
    boolean mOnCreateFinished;
    NoiseInfoProvider mNoiseInfoProvider;
    GPSInfoProvider mGPSInfoProvider;
    WifiInfoProvider mWifiInfoProvider;
    BluetoothInfoProvider mBluetoothInfoProvider;

    // Le service va rouler à une intervalle donnée. Le but est de récupérer les données voulues puis
    // s'arrête de lui-même.
    public static void startRecurrence(Context context)
    {
        Intent intent = new Intent(context, BigDataService.class);

        // Si L'intent n'est pas déjà scédulé, on le crée
        if(PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) == null)
        {
            PendingIntent scheduledIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager scheduler = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * REPETITION_DELAY, scheduledIntent);
        }
    }

    @Override
    public void onCreate()
    {
        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Log.v("BigDataService", "BigDataService service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.w("BigDataService", "BigDataService service has started");

        mContext = this;
        mOnCreateFinished = false;

        mGPSInfoProvider = new GPSInfoProvider(mContext, new DataReadyListener() {
            @Override
            public void dataReady(Object data) {
                if(data != null && data.getClass() == LocationData.class) {
                    LocationData locationData = (LocationData) data;
                    try {
                        locationData.checkForWriting();
                    } catch (Exception e) {
                        Log.d("BDEM_ERROR", e.getMessage());
                    }
                }
                mGPSInfoProvider = null;
                checkAllReceived();
            }
        });

        mWifiInfoProvider = new WifiInfoProvider(mContext, new DataReadyListener() {
            @Override
            public void dataReady(Object data) {
                if(data != null && data.getClass() == WifiData.class) {
                    WifiData wifiData = (WifiData) data;
                    try {
                        wifiData.checkForWriting();
                    } catch (Exception e) {
                        Log.d("BDEM_ERROR", e.getMessage());
                    }
                }
                mWifiInfoProvider = null;
                checkAllReceived();
            }
        });

        mBluetoothInfoProvider = new BluetoothInfoProvider(mContext, new DataReadyListener() {
            @Override
            public void dataReady(Object data) {
                if(data != null && data.getClass() == BluetoothData.class) {
                    BluetoothData bluetoothData = (BluetoothData) data;
                    try {
                        bluetoothData.checkForWriting();
                    } catch (Exception e) {
                        Log.d("BDEM_ERROR", e.getMessage());
                    }
                }
                mBluetoothInfoProvider = null;
                checkAllReceived();
            }
        });

        mOnCreateFinished = true;

        return Service.START_STICKY;
    }

    private void checkAllReceived() {
        if (
            mNoiseInfoProvider == null &&
            mGPSInfoProvider == null &&
            mWifiInfoProvider == null &&
            mBluetoothInfoProvider == null &&
            mOnCreateFinished
        ) {
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent arg) {
        Log.i("BigDataService", "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("BigDataService", "Service onDestroy");
    }
}