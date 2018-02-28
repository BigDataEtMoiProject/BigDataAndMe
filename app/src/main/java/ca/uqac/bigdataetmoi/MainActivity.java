package ca.uqac.bigdataetmoi;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.activity.CompteurDePasActivity;
import ca.uqac.bigdataetmoi.activity.GPSMapsActivity;
import ca.uqac.bigdataetmoi.activity.PermissionManagerActivity;
import ca.uqac.bigdataetmoi.activity.quizz_activity.QuizzActivity;
import ca.uqac.bigdataetmoi.activity.SommeilActivity;
import ca.uqac.bigdataetmoi.activity.TelephoneSmsActivity;
import ca.uqac.bigdataetmoi.activity.TempsUtilisationActivity;
import ca.uqac.bigdataetmoi.activity.app_usage_activity.DonneesUtilisationActivity;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.service.BigDataService;
import ca.uqac.bigdataetmoi.service.UpdateLockInfoService;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS;

public class MainActivity extends BaseActivity
{
    private ListView mFonctionsListView;
    private ArrayAdapter<String> mFonctionListAdapter;
    public final static BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //démarre le service de surveillance du temps d'utilisation
        //Toast.makeText(this, "demande de creation...", Toast.LENGTH_LONG).show();
        //Intent alerteService = new Intent(MainActivity.this, UpdateLockInfoService.class);
        //alerteService.putExtra("screenState", false);
        //startService(alerteService);

        // On crée les managers afin qu'il puisse charger les données de la bd
        DatabaseManager.getInstance();
        PermissionManager.getInstance();

        // Récupérer les vues du Layout
        mFonctionsListView = (ListView) findViewById(R.id.fonctionsListView);

        // On peuple le listView
        String[] fonctions = new String[] {
                "Gestion des permissions",
                "Compteur de pas",
                "Temps d'utilisation",
                "Sommeil",
                "Lieux (GPS)",
                "Téléphone et sms",
                "Données d'utilisation",
                "Quizz"
              };

        ArrayList<String> fonctionList = new ArrayList<String>();
        fonctionList.addAll(Arrays.asList(fonctions));

        mFonctionListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fonctionList);

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
                        startActivity(new Intent(MainActivity.this, TempsUtilisationActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, SommeilActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, GPSMapsActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, TelephoneSmsActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this, DonneesUtilisationActivity.class));
                        break;
                    case 7:
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
            ActivityCompat.requestPermissions(this, new String[] { ACTION_USAGE_ACCESS_SETTINGS }, 0);
        }


        // Le service de l,application est censé être démarré automatiquement lors du démarrage du système,
        // mais on le démarre ici quand-même au cas ou il aurait été arrêté.
        Intent serviceIntent = new Intent(getApplicationContext(), BigDataService.class);
        getApplicationContext().startService(serviceIntent);
    }
}
