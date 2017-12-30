package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;


import java.util.ArrayList;
import java.util.Arrays;

import ca.uqac.bigdataetmoi.activity.CompteurDePasActivity;
import ca.uqac.bigdataetmoi.activity.GPSMapsActivity;
import ca.uqac.bigdataetmoi.activity.QuizzActivity;
import ca.uqac.bigdataetmoi.activity.SommeilActivity;
import ca.uqac.bigdataetmoi.activity.TelephoneSmsActivity;
import ca.uqac.bigdataetmoi.activity.TempsUtilisationActivity;
import ca.uqac.bigdataetmoi.activity.data_usage_activity.DonneesUtilisationActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS;

public class MainActivity extends AppCompatActivity
{
    private ListView mFonctionsListView;
    private ArrayAdapter<String> mFonctionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer les vues du Layout
        mFonctionsListView = (ListView) findViewById(R.id.fonctionsListView);

        // On peuple le listView
        String[] fonctions = new String[] {
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
                        startActivity(new Intent(MainActivity.this, CompteurDePasActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, TempsUtilisationActivity.class));
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


        //Il faut demander l'accès au GPS TODO: Améliorer la gestion des permissions
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { ACCESS_FINE_LOCATION }, 0);
        }
        if (ContextCompat.checkSelfPermission(this, ACTION_USAGE_ACCESS_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { ACTION_USAGE_ACCESS_SETTINGS }, 0);
        }


        // Le service de l,application est censé être démarré automatiquement lors du démarrage du système,
        // mais on le démarre ici quand-même au cas ou il aurait été arrêté.
        Intent serviceIntent = new Intent(getApplicationContext(), BigDataService.class);
        getApplicationContext().startService(serviceIntent);
    }
}
