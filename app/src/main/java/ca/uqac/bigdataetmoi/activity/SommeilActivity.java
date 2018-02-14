package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data_models.AccelSensorData;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.data_models.LightSensorData;
import ca.uqac.bigdataetmoi.database.data_models.ProximitySensorData;

public class SommeilActivity extends BaseActivity {

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

