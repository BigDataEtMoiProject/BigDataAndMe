package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.bigdataetmoi.R;

@SuppressWarnings("HardCodedStringLiteral")
public class GPSMapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mLocationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocationsRef = DatabaseManager.getInstance().getDbRef(LocationData.DATA_ID);

        setContentView(R.layout.activity_gpsmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //fetch all locations and print them
        /*
        mLocationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<LatLng> locations = new ArrayList<LatLng>();
                for (DataSnapshot locationSnapshot: dataSnapshot.getChildren()) {
                    LatLng tmp = new LatLng(
                        locationSnapshot.child("latitude").getValue(double.class),
                        locationSnapshot.child("longitude").getValue(double.class)
                    );
                    String time = locationSnapshot.child("time").getValue(long.class).toString();
                    locations.add(tmp);
                    mMap.addMarker(new MarkerOptions().position(tmp).title(time));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(tmp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GPSMapsActivity.this, "ERROR : Cannot retrieve data locations ; Operation cancelled", Toast.LENGTH_LONG).show();
            }
        });
        */
    }
}
