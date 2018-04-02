package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
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
import ca.uqac.bigdataetmoi.activity.contact_sms.TelephoneSmsActivity;
import ca.uqac.bigdataetmoi.activity.TempsUtilisationActivity;
import ca.uqac.bigdataetmoi.activity.app_usage_activity.DonneesUtilisationActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;

import static android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS;

public class MainActivity extends BaseActivity
{
    private ListView mFonctionsListView;
    private ArrayAdapter<String> mFonctionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On met l'identifieur du téléphone dans la classe MainApplication
        MainApplication.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

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

        Log.w("BIG DATA Id : ", MainApplication.getUserId());

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

        // TODO : À quoi sert cette permission ??
        if (ContextCompat.checkSelfPermission(this, ACTION_USAGE_ACCESS_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { ACTION_USAGE_ACCESS_SETTINGS }, 0);
        }

        BigDataService.startRecurrence(getApplicationContext());
    }
}
