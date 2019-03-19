package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.data.PodometerData;

/**
 * Created by fs77 on 27/12/2017.
 * Modifié par patrick lapointe le 2018-03-15
 * Récupération des données du podomètre et écriture dans la base de données
 */

// On utilise le senseur TYPE_STEP_COUNTER de android pour compter les pas.
// Ce senseur retourne le nombre de pas depuis le dernier démarrage du système.
// Pour le moment, on fait juste stocker ces données, nous pourrions interpréter ces données plus tard
// afin de déterminer le nombre de pas pour un interval donné.

public class PodometerInfoProvider implements SensorEventListener
{
    private DataReadyListener mListener;

    private SensorManager mSensorManager;

    public PodometerInfoProvider(Context context, DataReadyListener listener)
    {
        mListener = listener;

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Sensor podoSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this, podoSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        mSensorManager.unregisterListener(this);

        PodometerData data = new PodometerData(null, sensorEvent.values[0]);
        mListener.dataReady(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
