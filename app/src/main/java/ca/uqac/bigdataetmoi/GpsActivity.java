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
    DatabaseReference dbRef;


    static final int REQUEST_LOCATIONS = 1111;
    final int MIN_TIMER_INTERVAL_BETWEEN_LOCATIONS = 5000;
    final int MIN_DISTANCE_INTERVAL_BETWEEN_LOCATIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        db = FirebaseDatabase.getInstance();


        GPSData = (TextView) findViewById(R.id.GPSData);
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> allGPSSensor = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        Sensor gpsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if(gpsSensor == null)
            Toast.makeText(getApplicationContext(), "No GPS found !", Toast.LENGTH_SHORT).show();
        else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if(locationManager == null)
                Toast.makeText(getBaseContext(), "location manager is null", Toast.LENGTH_SHORT).show();
            else {
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        storeLocationData(location);
                    }
                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {}
                    @Override
                    public void onProviderEnabled(String s) {}
                    @Override
                    public void onProviderDisabled(String s) {}
                };

                boolean accessCoarseLocation = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                boolean accessFineLocation = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

                if(accessCoarseLocation && accessFineLocation){
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastKnownLocation != null){
                        Toast.makeText(getBaseContext(), "using last known location", Toast.LENGTH_LONG).show();
                        GPSData.setText("last known location : lat= " + lastKnownLocation.getLatitude() + " long= " + lastKnownLocation.getLongitude());
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIMER_INTERVAL_BETWEEN_LOCATIONS, MIN_DISTANCE_INTERVAL_BETWEEN_LOCATIONS, locationListener);

                        storeLocationData(lastKnownLocation);
                    } else
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIMER_INTERVAL_BETWEEN_LOCATIONS, MIN_DISTANCE_INTERVAL_BETWEEN_LOCATIONS, locationListener);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATIONS);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissions denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private boolean storeLocationData(Location loc){
        String longitude = "long= " + loc.getLongitude();
        String latitude = "lat= " + loc.getLatitude();

        Log.v("LOCATION", "location changed : " + longitude + " " + latitude);
        Toast.makeText(getBaseContext(), "Location changed : " + latitude + " " + longitude, Toast.LENGTH_LONG).show();

//        db.getReference("users").child(userID).child("locations").child("0").child("latitude").setValue(loc.getLatitude());
//        db.getReference("users").child(userID).child("locations").child("0").child("longitude").setValue(loc.getLongitude());

        String key = db.getReference("users").child(userID).child("locations").push().getKey();
        Map<String, Object> update = new HashMap<>();
        update.put("/" + userID + "/locations/" + key, loc);
        db.getReference("users").updateChildren(update);

        return true;
    }
}
