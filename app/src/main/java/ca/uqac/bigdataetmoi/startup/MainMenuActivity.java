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

public class MainMenuActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //On assigne l'acitivite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);

        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        BigDataService.startRecurrence(getApplicationContext());
    }
}
