package ca.uqac.bigdataetmoi.service.threads;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ca.uqac.bigdataetmoi.database.SensorDataCollection;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

/**
 * Created by patrick on 2017-12-19.
 * Cette classe à pour but la récupération des capteurs de base, c'est à dire ceux qui utilisent le SensorManager
 */

@SuppressWarnings("HardCodedStringLiteral")
public class BasicSensorThread extends Thread implements Runnable, SensorEventListener {

    private final long SENSOR_MIN_UPDATE_MILLIS = 120000; //Temps minimum entre deux écriture dans la BD (2 minutes)

    private DatabaseManager dbManager;
    private boolean mAccelWasMoving; // On garde en m^moire si oui ou non on a bougé lors du dernier interval
    private float mLightLuxLevel;
    private float mProximityDistance;
    private float mAccel, mAccelCurrent, mAccelLast;

    public BasicSensorThread(Context context) {

        dbManager = DatabaseManager.getInstance();

        mAccelWasMoving = false;
        mLightLuxLevel = -1;
        mProximityDistance = -1;

        SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

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
                Thread.sleep(SENSOR_MIN_UPDATE_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dbManager.storeSensorDataCollection(new SensorDataCollection(mAccelWasMoving, mLightLuxLevel, mProximityDistance));
            mAccelWasMoving = false;
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLightLuxLevel = event.values[0];
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = event.values.clone();
            if(checkIfMooving(gravity[0], gravity[1], gravity[2]))
                mAccelWasMoving = true;
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            mProximityDistance = event.values[0];
        }
    }

    private boolean checkIfMooving(float accelX, float accelY, float accelZ)
    {
        // Détection d'un changement d'accélération (cela veut dire que le téléphone bouge)
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(accelX*accelX + accelY*accelY + accelZ*accelZ);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        // Cela veut dire qu'on bouge
        return mAccel > 1.0;
    }
}
