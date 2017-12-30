package ca.uqac.bigdataetmoi.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import ca.uqac.bigdataetmoi.activity.CompteurDePasActivity;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.PodoSensorData;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by fs77 on 27/12/2017.
 */

//Cette classe est un thread qui permet de stocker les données récupérées dans le  podomètre dans la base de données

public class PodoSensorThread extends Thread implements Runnable,SensorEventListener {

    private Sensor mySensor;
    private SensorManager SM;
    private float mStepOffset;
    DatabaseManager dbManager;
    private long mPrevPodoMillis;
    Context mContext;



    public PodoSensorThread(Context context) {
        mContext = context;
        mStepOffset = 0;
        mPrevPodoMillis =System.currentTimeMillis(); ;
        dbManager = DatabaseManager.getInstance();
        SM = (SensorManager)mContext.getSystemService(context.SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        SM.registerListener((SensorEventListener) this, mySensor , SensorManager.SENSOR_DELAY_NORMAL);
        CompteurDePasActivity mainActivity = new CompteurDePasActivity();
        mainActivity.nb = 0;

    }


    @Override
    public void run() {

        while(true) {
        }
    }






    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (0 == mStepOffset)
            mStepOffset = sensorEvent.values[0];

        CompteurDePasActivity.nb = sensorEvent.values[0] - mStepOffset;


        long currMillis = System.currentTimeMillis();

        //On met à jour les mouvement si celui-ci a lieu au minimum 24 heures plus tard
        if (currMillis - mPrevPodoMillis > 86400000) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            dbManager.storePodoSensorData(new PodoSensorData(timeStamp, CompteurDePasActivity.nb));
            mPrevPodoMillis = currMillis;
            mStepOffset = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
