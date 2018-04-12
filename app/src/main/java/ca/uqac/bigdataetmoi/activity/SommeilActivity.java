package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.service.data_interpretation.SommeilInfo;

public class SommeilActivity extends BaseActivity {

    private TextView mLuxTextView, mInterLuxTextView, mAccelTextView, mProximiTextView;
    DatabaseManager dbManager;
    SommeilInfo sommeilInfo = new SommeilInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sommeil);

        //ajouter des valeurs en manuel
        /*DatabaseManager dbManager;
        dbManager = DatabaseManager.getInstance();
        dbManager.storeSommeilCalculationData(130);*/

        //Récupération des views
        mLuxTextView = (TextView) findViewById(R.id.luxTextView);
        mInterLuxTextView = (TextView) findViewById(R.id.interLuxTextView);
        mAccelTextView = (TextView) findViewById(R.id.accelTextView);
        mProximiTextView = (TextView) findViewById(R.id.proximiTextView);

        //Pour lecture de la bd
        dbManager = DatabaseManager.getInstance();

        Query query = dbManager.getCalculationDbRef();
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int moyenne = 0;    int moyenneMois = 0;    int moyenneSemaine = 0;
                int cmptMoyenne = 0;    int cmptMois = 0;   int cmptSemaine = 0;

                // On retrouve la donnée
                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    Integer data = child.getValue(Integer.class);

                    Log.i("Temps de sommeil",child.getKey() + " -> " + data);

                    // calcul de la moyenne
                    moyenne += data;
                    cmptMoyenne++;

                    // calcul du mois dernier
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH,-1);
                    Date moisDernier = cal.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat ("MM");
                    String moisDernierString = formatter.format(moisDernier);
                    if (child.getKey().substring(5,7).equals(moisDernierString)){
                        moyenneMois += data;
                        cmptMois++;
                    }

                    //calcul semaine derniere
                    Calendar cal2 = Calendar.getInstance();
                    int week = cal.get(Calendar.WEEK_OF_YEAR);
                    cal2.set(Calendar.WEEK_OF_YEAR,week-1);
                    Date semaineDerniereFin = cal2.getTime();
                    cal2.set(Calendar.WEEK_OF_YEAR,week-2);
                    Date semaineDerniereDebut = cal2.getTime();
                    //SimpleDateFormat formatter2 = new SimpleDateFormat ("dd");
                    //String semaineDerniereDebutString = formatter2.format(semaineDerniereDebut);
                    //String semaineDerniereFinString = formatter2.format(semaineDerniereFin);
                    Log.i("TEST",semaineDerniereDebut.toString());
                    Log.i("TEST",semaineDerniereFin.toString());
                    Log.i("TEST",child.getKey());
                    for(int i=1; i<=10 ;i++){

                    }
                    if (child.getKey().equals(semaineDerniereDebut)){
                        moyenneSemaine += data;
                        cmptSemaine++;
                    }

                    // On trouve le temps de sommeil d'hier
                    Calendar cal3 = Calendar.getInstance();
                    cal3.add(Calendar.DATE, -1);
                    Date hier = cal3.getTime();
                    SimpleDateFormat formatter3 = new SimpleDateFormat ("yyyy MM dd");
                    String hierString = formatter3.format(hier);
                    if (child.getKey().equals(hierString))   mProximiTextView.setText("Temps de sommeil: " + data.toString() + " minutes");
                }

                try {
                    //affchage de la moyenne
                    int res = moyenne/cmptMoyenne;
                    mLuxTextView.setText("Temps de sommeil: " + res + " minutes");

                    //affichage du mois dernier
                    int res2 = moyenneMois/cmptMois;
                    mInterLuxTextView.setText("Temps de sommeil: " + res2 + " minutes");

                    //affichage du mois dernier
                    int res3 = moyenneSemaine/cmptSemaine;
                    mAccelTextView.setText("Temps de sommeil: " + res3 + " minutes");
                }catch (Exception e){
                    Log.i("ERROR",e.getMessage());
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Récupérer les données de la bd (dernier enregistrement)

    }
}

