package ca.uqac.bigdataetmoi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.widget.TextView;

public class SommeilActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight, mAccelerometer;
    private TextView mLuxTextView, mInterLuxTextView, mAccelTextView, mInterAccelTextView;
    private float mAccel, mAccelCurrent, mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sommeil);

        //Récupération des views
        mLuxTextView = (TextView) findViewById(R.id.luxTextView);
        mInterLuxTextView = (TextView) findViewById(R.id.interLuxTextView);
        mAccelTextView = (TextView) findViewById(R.id.accelTextView);
        mInterAccelTextView = (TextView) findViewById(R.id.interAccelTextView);

        //Initialisation des senseurs
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            float lux = event.values[0];
            mLuxTextView.setText(String.valueOf(lux));
            mInterLuxTextView.setText(interpreterLux(lux));
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float[] gravity = event.values.clone();
            float x = gravity[0];
            float y = gravity[1];
            float z = gravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            mAccelTextView.setText(String.valueOf(mAccel));
            mInterAccelTextView.setText(interpreterAccel(mAccel));
        }
    }

    String interpreterLux(float lux)
    {
        String text;

        if(lux <= 10.0)
            text = "Noir";
        else if(lux <= 30.0)
            text = "Sombre";
        else if(lux <= 125)
            text = "Faible éclairage";
        else if(lux <= 300)
            text = "Éclairage moyen";
        else if(lux <= 700)
            text = "Éclairage normal";
        else if(lux <= 7500)
            text = "Clair";
        else
            text = "Très clair";

        return text;
    }

    String interpreterAccel(float accel)
    {
        String text;

        if(accel > 1.0)
            text = "En mouvement";
        else
            text = "Aucun mouvement";

        return text;
    }
}

