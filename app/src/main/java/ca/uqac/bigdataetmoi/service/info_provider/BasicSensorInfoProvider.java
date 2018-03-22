package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import ca.uqac.bigdataetmoi.database.DataCollection;

/**
 * Created by patrick on 2017-12-19.
 * Cette classe à pour but la récupération des capteurs de base, c'est à dire ceux qui utilisent le SensorManager
 */

public class BasicSensorInfoProvider extends InfoProvider implements SensorEventListener {
    private Boolean mAccelIsMoving; // On garde en memoire si oui ou non on a bougé lors du dernier interval
    private Float mLightLuxLevel;
    private Float mProximityDistance;
    private float mAccel, mAccelCurrent, mAccelLast;

    private SensorManager mSensorManager;

    public BasicSensorInfoProvider(Context context)
    {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour l'accéléromètre
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Pour le capteur de proximité
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLightLuxLevel = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = event.values.clone();
            if(checkIfMooving(gravity[0], gravity[1], gravity[2]))
                mAccelIsMoving = true;
            else if(mAccelIsMoving == null)
                mAccelIsMoving = false;
            // TODO : Les données de mouvement vont peut-être etre erronées vu qu'il faut que le téléphone soit en mouvement au moment exacte ou on récupère le senseur.
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            mProximityDistance = event.values[0];
        }

        // On verifie que le capteur de lumière n'est pas leurré par la proximité (exemple: telephone dans la poche, dans un sac)
        // Lorsque l'on a lu toutes les données, on arrête d'écouter et on envoie les données.
        if(mLightLuxLevel >= 10 /*lux*/ && mAccelIsMoving != null && mProximityDistance >= 15 /*cm*/) {
            mSensorManager.unregisterListener(this);

            DataCollection collection = new DataCollection();
            collection.isMoving = mAccelIsMoving;
            collection.luxLevel = mLightLuxLevel;
            collection.proximityDistance = mProximityDistance;

            generateDataReadyEvent(collection);
        }
    }

    private boolean checkIfMooving(float accelX, float accelY, float accelZ)
    {
        // Détection d'un changement d'accélération (cela veut dire que le téléphone bouge)
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(accelX*accelX + accelY*accelY + accelZ*accelZ);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if(mAccel > 1.0) { // Cela veut dire qu'on bouge
            return true;
        }
        else
            return false;
    }
}
