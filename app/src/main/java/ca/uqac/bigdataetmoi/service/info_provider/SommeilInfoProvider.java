package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;

import ca.uqac.bigdataetmoi.database.data.SommeilData;

/**
 * Created by Patrick Lapointe on 2018-05-05.
 * Récupération des données relatives au sommeil afin de détecter si la personne dors ou non
 */

public class SommeilInfoProvider implements SensorEventListener
{
    private static final int TEMPS_ECHANTILLONAGE_ACCEL = 15; // Nombre de secondes durant lesquels on vérifie si le téléphone est en mouvement

    private DataReadyListener mListener;

    private Float mLightLuxLevel;
    private Float mProximityDistance;
    private Boolean mScreenState;
    private Double mSoundLevel;

    private Boolean mAccelIsMoving; // On garde en memoire si oui ou non on a bougé.
    private float mAccel, mAccelCurrent, mAccelLast;

    private SensorManager mSensorManager;
    private Handler mHandler;
    private Runnable mRunnable;

    public SommeilInfoProvider(Context context, Double soundLevel, DataReadyListener listener)
    {
        mListener = listener;
        mSoundLevel = soundLevel;

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        // Pour la lumière
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour le capteur de proximité
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Pour l'accéléromètre, on observe l'accéléromètre durant quelques secondes
        // afin de détecter un mouvement. Aussitot que l'on détecte un mouvement,
        // on arrête l'observation et on considère que le téléphone bouge.
        // Si, par contre, aucun mouvement n'est détecté durant la période d'observation,
        // On considère que le téléphone ne bouge pas.
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mAccelIsMoving = false;
                dataUpdated();
            }
        };

        mHandler.postDelayed(mRunnable, TEMPS_ECHANTILLONAGE_ACCEL * 1000);

        // Vérification de l'état de l'écran (allumée ou non)
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= 20 && powerManager.isInteractive())
            mScreenState = true;
        else
            mScreenState = false;

        dataUpdated();
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT && mLightLuxLevel == null)
        {
            //récupération des données du capteur de lumière
            mLightLuxLevel = event.values[0];
            dataUpdated();
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY && mProximityDistance == null)
        {
            //récupération des données du capteur de proximité
            mProximityDistance = event.values[0];
            dataUpdated();
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mAccelIsMoving == null)
        {
            // Verification si la personne bouge
            float[] gravity = event.values.clone();
            if(checkIfMooving(gravity[0], gravity[1], gravity[2])) {
                mHandler.removeCallbacks(mRunnable);
                mAccelIsMoving = true;
                dataUpdated();
            }
        }
    }

    // On vérifie si le téléhpone est en mouvement selon les les valeures d'accélération passées en paramètre
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

    // Cette fonction est appelé lorsque des données supplémentaires sont trouvées.
    // Si on a tous ce qu'il faut pour savoir si la personne dors ou non, on fait
    // la vérification puis on écris cela dans la bd
    private void dataUpdated()
    {
        // On vérifie si on a tous les données
        if(mLightLuxLevel != null && mProximityDistance != null && mScreenState != null && mAccelIsMoving != null)
        {
            // On verifie que le capteur de lumière n'est pas leurré par la proximité (exemple: telephone dans la poche, dans un sac)
            // Si ses le cas, on ne peut pas déterminer si la personne dors ou non
            if(!(mLightLuxLevel <= 10 /*lux*/ && mProximityDistance <= 3 /*cm*/)) {
                // On écris rien dans la bd car données non-fiables
                mListener.dataReady(null);
            }
            else {
                Boolean isSleeping;
                // On vérifie si la personne dors selon les informations recus
                if(!mAccelIsMoving && !mScreenState && mLightLuxLevel <= 10 && mProximityDistance >= 15 && mSoundLevel <= 10)
                    isSleeping = true;
                else
                    isSleeping = false;

                SommeilData data = new SommeilData(null, isSleeping);
                mListener.dataReady(data);
            }

        }
    }


}
