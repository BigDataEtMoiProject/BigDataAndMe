package ca.uqac.bigdataetmoi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GpsActivity extends AppCompatActivity {
    String userID = "0";

    TextView GPSData;
    LocationManager locationManager;
    FirebaseDatabase db;
    DatabaseReference locationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        db = FirebaseDatabase.getInstance();
        userID = ((MainApplication) this.getApplication()).getUserId();
        locationsRef = db.getReference("users/" + userID + "/locations");

        //setup layout
        GPSData = (TextView) findViewById(R.id.GPSData);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //fetch last stored updated location and print it
        locationsRef.orderByChild("time").addChildEventListener(new ChildEventListener() {
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
