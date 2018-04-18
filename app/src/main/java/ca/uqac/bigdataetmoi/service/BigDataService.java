package ca.uqac.bigdataetmoi.service;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import ca.uqac.bigdataetmoi.service.data_interpretation.SommeilInfo;
import ca.uqac.bigdataetmoi.service.info_provider.AccelerometerInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.ScreenStateInfoProvider;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.service.info_provider.BasicSensorInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.BluetoothInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;
import ca.uqac.bigdataetmoi.service.info_provider.GPSInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.InfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.MicroInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.PodometerInfoProvider;
import ca.uqac.bigdataetmoi.service.info_provider.WifiInfoProvider;

/**
* Créé le 2017-11-16 par Patrick Lapointe
* Service qui récupère les infos des différents capteurs et qui envoie les données à la base de donnée.
*/

public class BigDataService extends Service implements DataReadyListener
{
    private final static int REPETITION_DELAY = 5; // Mise à jour des données au x minutes
    private final static int MAXIMUM_WAITING_TIME = 45; // On arrête le service si nous avons attendu plus de x secondes

    private List<InfoProvider> mProviders = new ArrayList<>();
    private DataCollection mDataCollection;
    Handler mHandler;
    Runnable mRunnable;

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
        mDataCollection = new DataCollection();

        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Log.v("BigDataService", "BigDataService service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.w("BigDataService", "BigDataService service has started");

        BasicSensorInfoProvider basicSensorProvider = new BasicSensorInfoProvider(this);
        basicSensorProvider.addDataReadyListener(this);
        mProviders.add(basicSensorProvider);

        AccelerometerInfoProvider accelerometerInfoProvider = new AccelerometerInfoProvider(this);
        accelerometerInfoProvider.addDataReadyListener(this);
        mProviders.add(accelerometerInfoProvider);

        ScreenStateInfoProvider screenStateInfoProvider = new ScreenStateInfoProvider(this);
        screenStateInfoProvider.addDataReadyListener(this);
        mProviders.add(screenStateInfoProvider);

        PodometerInfoProvider podometerInfoProvider = new PodometerInfoProvider(this);
        podometerInfoProvider.addDataReadyListener(this);
        mProviders.add(podometerInfoProvider);

        GPSInfoProvider GPSInfoProvider = new GPSInfoProvider(this);
        GPSInfoProvider.addDataReadyListener(this);
        mProviders.add(GPSInfoProvider);

        WifiInfoProvider wifiInfoProvider = new WifiInfoProvider(this);
        wifiInfoProvider.addDataReadyListener(this);
        mProviders.add(wifiInfoProvider);

        BluetoothInfoProvider bluetoothInfoProvider = new BluetoothInfoProvider(this);
        bluetoothInfoProvider.addDataReadyListener(this);
        mProviders.add(bluetoothInfoProvider);

        MicroInfoProvider microInfoProvider = new MicroInfoProvider();
        microInfoProvider.addDataReadyListener(this);
        mProviders.add(microInfoProvider);

        // On n'attends pas nécéssairement d'avoir toutes les données,
        // on écris dans la bd après un délais prédéterminé.
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                writeData();
            }
        };
        mHandler.postDelayed(mRunnable, MAXIMUM_WAITING_TIME * 1000);



        // On vérifie que les calculs du temps de sommeil pour la journée précédente ont été fait.
        // Si c'est pas le cas, on effectue le calcul. (cela se fait à l'interne de la classe)
        new SommeilInfo();

        return Service.START_STICKY;
    }

    @Override
    public void dataReady(Object data) {
        // Stocker les données dans le SensorDataCollection
        if(data.getClass() == DataCollection.class)
            mDataCollection.receiveData((DataCollection)data);
        else
            return;

        // Si on a stocké tous les données, il est temps d'enregistrer le tout dans la bd
        if(mDataCollection.allDataReceived())
        {
            mHandler.removeCallbacks(mRunnable);
            writeData();
        }
    }

    @Override
    public IBinder onBind(Intent arg) {
        Log.i("BigDataService", "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        for (InfoProvider provider : mProviders)
            provider.unregisterDataReadyListener(this);

        Log.i("BigDataService", "Service onDestroy");
    }

    private void writeData() {
        // Stockage dans la bd
        DatabaseManager.getInstance().storeSensorDataCollection(mDataCollection);
        // On arrête le service
        stopSelf();
    }
}