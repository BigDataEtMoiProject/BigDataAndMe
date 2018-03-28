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
    private Float mLightLuxLevel;
    private Float mProximityDistance;

    private SensorManager mSensorManager;

    public BasicSensorInfoProvider(Context context)
    {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

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
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            mProximityDistance = event.values[0];
        }

        // Si tous les données de sensuers ont été recues
        if (mLightLuxLevel != null && mProximityDistance != null)
            envoyerDonnees();
    }

    private void envoyerDonnees()
    {
        // On verifie que le capteur de lumière n'est pas leurré par la proximité (exemple: telephone dans la poche, dans un sac)
        // Lorsque l'on a lu toutes les données, on arrête d'écouter et on envoie les données.
        if(!(mLightLuxLevel <= 10 /*lux*/ && mProximityDistance <= 3 /*cm*/)) {
            mSensorManager.unregisterListener(this);

            DataCollection collection = new DataCollection();
            collection.luxLevel = mLightLuxLevel;
            collection.proximityDistance = mProximityDistance;

            generateDataReadyEvent(collection);
        }
    }
}
