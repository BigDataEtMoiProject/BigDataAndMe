package ca.uqac.bigdataetmoi.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

import ca.uqac.bigdataetmoi.database.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.LightSensorData;
import ca.uqac.bigdataetmoi.database.ProximitySensorData;

/**
 * Created by patrick on 2017-12-19.
 * Cette classe à pour but la récupération des capteurs de base, c'est à dire ceux qui utilisent le SensorManager
 */

public class BasicSensorThread extends Thread implements Runnable, SensorEventListener {

    final int SENSOR_MIN_UPDATE_MILLIS = 300000; //Temps minimum entre deux écriture dans la BD (5 minutes)

    DatabaseManager dbManager;
    Context mContext;
    private long mPrevAccelMillis, mPrevMoveMillis, mPrevProximityMillis;
    private float mAccel, mAccelCurrent, mAccelLast;

    public BasicSensorThread(Context context) {
        mContext = context;

        dbManager = DatabaseManager.getInstance();

        mPrevMoveMillis = 0;
        mPrevAccelMillis = 0;
        mPrevProximityMillis = 0;

        SensorManager sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour l'accéléromètre
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Pour le capteur de proximité
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void run() {

        while(true) {
            System.out.println("BasicSensorThread.run()");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            handleLightSensorChanged(event.values[0]);
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = event.values.clone();
            handleAccelSensorChanged(gravity[0], gravity[1], gravity[2]);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            handleProximitySensorChanged(event.values[0]);
        }
    }

    // Gestion des changements de valeurs des capteurs

    private void handleLightSensorChanged(float newLux)
    {
        long currMillis = System.currentTimeMillis();

        if(currMillis - mPrevAccelMillis > SENSOR_MIN_UPDATE_MILLIS) {
            dbManager.storeSensorData(new LightSensorData(Calendar.getInstance().getTime(), newLux));
            mPrevAccelMillis = currMillis;
        }
    }

    private void handleAccelSensorChanged(float accelX, float accelY, float accelZ)
    {
        // Détection d'un changement d'accélération (cela veut dire que le téléphone bouge)
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(accelX*accelX + accelY*accelY + accelZ*accelZ);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if(mAccel > 1.0) { // Cela veut dire qu'on bouge
            long currMillis = System.currentTimeMillis();

            //On met à jour les mouvement si celui-ci a lieu au minimum 5 minutes plus tard
            if(currMillis - mPrevMoveMillis > SENSOR_MIN_UPDATE_MILLIS){
                dbManager.storeSensorData(new AccelSensorData(Calendar.getInstance().getTime(), true));
                mPrevMoveMillis = currMillis;
            }
        }
    }

    private void handleProximitySensorChanged(float newDistance)
    {
        long currMillis = System.currentTimeMillis();

        if(currMillis - mPrevProximityMillis > SENSOR_MIN_UPDATE_MILLIS){
            dbManager.storeSensorData(new ProximitySensorData(Calendar.getInstance().getTime(), newDistance));
            mPrevProximityMillis = currMillis;
        }
    }
}
