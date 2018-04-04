package ca.uqac.bigdataetmoi.startup;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;

public class MainMenu extends BaseActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient location;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //On assigne l'acitivite courante dans le Fetcher
        ActivityFetcher.setCurrentActivity(this);

        // On met l'identifieur du téléphone dans la classe ActivityFetcher
        ActivityFetcher.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(this);

        location = LocationServices.getFusedLocationProviderClient(this);

        BigDataService.startRecurrence(getApplicationContext());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Verification que les permissions sont accordees
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        map = googleMap;
        //Activation de la fonctionalite de googleMap pour le bouton de zoom sur la position actuelle
        map.setMyLocationEnabled(true);

        /*
            Recherche de la derniere position connu de google

            Dans quelques cas, la position sera null:
            1. Le senseur de localisation est desactive
            2. L'appareil a ete formate
            3. Google play service a ete restarte
         */
        location.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                            map.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }
                });

    }
}
