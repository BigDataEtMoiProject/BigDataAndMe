package ca.uqac.bigdataetmoi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

public class GpsActivity extends AppCompatActivity {
    String userID = "0";

    TextView GPSData;
    DatabaseReference mLocationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        mLocationsRef = DatabaseManager.getInstance().getLocationRef();

        //setup layout
        GPSData = (TextView) findViewById(R.id.GPSData);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //fetch last stored updated location and print it
        mLocationsRef.orderByChild("time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Map<String, Object> newLoc = (Map<String, Object>) dataSnapshot.getValue();
                String latitude = newLoc.get("latitude").toString();
                String longitude = newLoc.get("longitude").toString();
                String altitude = newLoc.get("altitude").toString();

                GPSData.setText("Latitude : " + latitude + "\nLongitude : " + longitude + "\nAltitude : " + altitude);
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
