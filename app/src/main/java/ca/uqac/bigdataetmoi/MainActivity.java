package ca.uqac.bigdataetmoi;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.service.BigDataService;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {
    private ListView mFonctionsListView;
    private ArrayAdapter<String> mFonctionListAdapter;
    public final static BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On crée les managers afin qu'il puisse charger les données de la bd
        DatabaseManager.getInstance();
        PermissionManager.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(this);

        /*
        // Récupérer les vues du Layout
        mFonctionsListView = findViewById(R.id.fonctionsListView);

        // On peuple le listView
        String[] fonctions = getResources().getStringArray(R.array.main_menu);

        ArrayList<String> fonctionList = new ArrayList<>();
        fonctionList.addAll(Arrays.asList(fonctions));

        mFonctionListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fonctionList);

        mFonctionsListView.setAdapter(mFonctionListAdapter);

        // On définie les actions lors d'un clic sur un item
        mFonctionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                        startActivity(new Intent(MainActivity.this, PermissionManagerActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, CompteurDePasActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, SommeilActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, GPSMapsActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, TelephoneSmsActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, DonneesUtilisationActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this, QuizzActivity.class));
                        break;
                }
            }
        });

        // Activation du Bluetooth
        if (BTAdapter != null && !BTAdapter.isEnabled()) {
            BTAdapter.enable();
        }

        // TODO : À quoi sert cette permission ??
        if (ContextCompat.checkSelfPermission(this, ACTION_USAGE_ACCESS_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(this, new String[] { ACTION_USAGE_ACCESS_SETTINGS }, 0);
            }
        }
        */

        // Le service de l'application est censé être démarré automatiquement lors du démarrage du système,
        // mais on le démarre ici quand-même au cas ou il aurait été arrêté.
        Intent serviceIntent = new Intent(getApplicationContext(), BigDataService.class);
        getApplicationContext().startService(serviceIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        googleMap.setMyLocationEnabled(true);
    }
}
