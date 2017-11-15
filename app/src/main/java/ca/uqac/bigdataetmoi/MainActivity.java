package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity
{
    private ListView mFonctionsListView;
    private ArrayAdapter<String> mFonctionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //démarre le service de surveillance du temps d'utilisation
        //Toast.makeText(this, "demande de creation...", Toast.LENGTH_LONG).show();
        Intent alerteService = new Intent(MainActivity.this, UpdateLockInfoService.class);
        alerteService.putExtra("screenState", false);
        startService(alerteService);


        // Récupérer les vues du Layout
        mFonctionsListView = (ListView) findViewById(R.id.fonctionsListView);

        // On peuple le listView
        String[] fonctions = new String[] {
                "Compteur de pas",
                "Temps d'utilisation",
                "Sommeil",
                "Lieux (GPS)",
                "Téléphone et sms",
                "Données d'utilisation"};

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
                        startActivity(new Intent(MainActivity.this, GpsActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, TelephoneSmsActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, DonneesUtilisationActivity.class));
                        break;
                }
            }
        });
    }
}



