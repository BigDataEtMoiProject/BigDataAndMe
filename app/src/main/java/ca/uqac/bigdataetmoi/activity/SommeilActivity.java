package ca.uqac.bigdataetmoi.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.Map;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.LightSensorData;
import ca.uqac.bigdataetmoi.database.ProximitySensorData;

public class SommeilActivity extends AppCompatActivity {

    private TextView mLuxTextView, mInterLuxTextView, mAccelTextView, mProximiTextView;
    DatabaseManager dmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sommeil);

        //Récupération des views
        mLuxTextView = (TextView) findViewById(R.id.luxTextView);
        mInterLuxTextView = (TextView) findViewById(R.id.interLuxTextView);
        mAccelTextView = (TextView) findViewById(R.id.accelTextView);
        mProximiTextView = (TextView) findViewById(R.id.proximiTextView);

        //Pour lecture de la bd
        dmManager = DatabaseManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Récupérer les données de la bd (dernier enregistrement

        ChildEventListener mDate = DatabaseManager.getInstance().getDbRef(AccelSensorData.DATA_ID).
                orderByChild("mDate").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                AccelSensorData data = (AccelSensorData) dataSnapshot.getValue(AccelSensorData.class);
                mAccelTextView.setText(data.isMoving() + " le " + data.getDate().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseManager.getInstance().getDbRef(LightSensorData.DATA_ID).
                orderByChild("mDate").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                LightSensorData data = (LightSensorData) dataSnapshot.getValue(LightSensorData.class);
                mLuxTextView.setText(data.getLux() + " le " + data.getDate().toString());
                mInterLuxTextView.setText(LightSensorData.interpreterLux(data.getLux()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        DatabaseManager.getInstance().getDbRef(ProximitySensorData.DATA_ID).
                orderByChild("mDate").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                ProximitySensorData data = (ProximitySensorData) dataSnapshot.getValue(ProximitySensorData.class);
                mProximiTextView.setText(data.getDistance() + " le " + data.getDate().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

}

