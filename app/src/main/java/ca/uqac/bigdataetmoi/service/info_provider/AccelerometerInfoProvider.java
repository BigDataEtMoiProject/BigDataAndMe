package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import ca.uqac.bigdataetmoi.database.DataCollection;

/**
 * Created by pat on 2018-03-28.
 */

public class AccelerometerInfoProvider extends InfoProvider implements SensorEventListener
{
    private static final int TEMPS_ECHANTILLONAGE = 30; // Nombre de secondes durant lesquels on vérifie si le téléphone est en mouvement

    private Boolean mAccelIsMoving; // On garde en memoire si oui ou non on a bougé.
    private float mAccel, mAccelCurrent, mAccelLast;

    private SensorManager mSensorManager;
    private Handler mHandler;
    private Runnable mRunnable;

    public AccelerometerInfoProvider(Context context)
    {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        // Pour l'accéléromètre
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                envoyerDonnees(false);
            }
        };

        mHandler.postDelayed(mRunnable, TEMPS_ECHANTILLONAGE * 1000);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] gravity = event.values.clone();
            if(checkIfMooving(gravity[0], gravity[1], gravity[2]))
            {
                mHandler.removeCallbacks(mRunnable);
                envoyerDonnees(true);
            }
        }
    }

    private void envoyerDonnees(boolean isMoving)
    {
        mSensorManager.unregisterListener(this);
        DataCollection collection = new DataCollection();
        collection.isMoving = isMoving;
        generateDataReadyEvent(collection);
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
