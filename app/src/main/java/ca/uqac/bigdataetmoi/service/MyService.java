package ca.uqac.bigdataetmoi.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.uqac.bigdataetmoi.activity.CompteurDePasActivity;
import ca.uqac.bigdataetmoi.database.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.PodoSensorData;

public class MyService extends Service implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    private float mStepOffset;
    DatabaseManager dbManager;
    private long mPrevPodoMillis;



    public MyService() {
    }

    @Override
    public void onCreate()
    {
        mStepOffset = 0;
        mPrevPodoMillis =System.currentTimeMillis(); ;
        dbManager = DatabaseManager.getInstance();
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        SM.registerListener((SensorEventListener) this, mySensor , SensorManager.SENSOR_DELAY_NORMAL);
        CompteurDePasActivity mainActivity = new CompteurDePasActivity();
        mainActivity.nb = 0;




    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {




        if (0 == mStepOffset)
            mStepOffset = sensorEvent.values[0];

        CompteurDePasActivity.nb = sensorEvent.values[0] - mStepOffset;


        long currMillis = System.currentTimeMillis();

        //On met à jour les mouvement si celui-ci a lieu au minimum 24 heures plus tard
        if (currMillis - mPrevPodoMillis > 60000) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            dbManager.storePodoSensorData(new PodoSensorData(timeStamp, CompteurDePasActivity.nb));
            mPrevPodoMillis = currMillis;
            mStepOffset = 0;
        }






    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {System.out.println("onStartCommand(Intent, int, int) called");
        return START_STICKY;}

    @Override
    public void onDestroy()
    {System.out.println("onDestroy() called");}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
