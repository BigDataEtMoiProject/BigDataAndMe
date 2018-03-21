package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

//Dans cette classe on affiche le nombre de pas en temps réel , la moyenne du nombre de pas ainsi que le nombre de pas durant les 7 derniers jours
//Appuyer sur no chart data available pour afficher le graphique au debut

@SuppressWarnings("HardCodedStringLiteral")
public class CompteurDePasActivity extends BaseActivity {


    TextView textView;
    public static float nb;
    TextView nb_pas_veille;
    TextView affichage_nbpas_moyenne;

    //graphique
    private LineChart mChart;
    ArrayList <Entry> yValues;

    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compteur_de_pas);

        textView = findViewById(R.id.txt);

        //Pour lecture de la bd
        dbManager = DatabaseManager.getInstance();

        //graphique
        mChart = findViewById(R.id.Linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        yValues = new ArrayList<>();

        //Ajouté
        affichage_nbpas_moyenne = (TextView) findViewById(R.id.moyenne_pas);
        nb_pas_veille = (TextView) findViewById(R.id.nb_pas_veille);
        nb_pas_veille.setText("");
        affichage_nbpas_moyenne.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Récupérer les données de la bd (dernier enregistrement
        Query query = dbManager.getSensorDataDbRef().orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // On retrouve la donnée
                if(dataSnapshot.hasChildren()) {
                    DataSnapshot child = dataSnapshot.getChildren().iterator().next();
                    long timestamp = Long.parseLong(child.getKey());
                    DataCollection data = child.getValue(DataCollection.class);

                    // Conversion du timestamp en date
                    Calendar cal = Calendar.getInstance(Locale.CANADA_FRENCH);
                    cal.setTimeInMillis(timestamp);
                    String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

                    // On rempli les champs de l'écran
                    textView.setText("      COMPTEUR DE PAS\n\n      Nb de pas aujourd'hui : " + Float.toString(data.steps) + "pas");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    /*
        Patrick lapointe
        Ancienne lecture de la bd, j'ai laissé le code pour ceux qui voudraient faire re-marcher la grille

    private void RecupererValeur() {
        nb_pas_veille.setText("");

        DatabaseReference dB = db.getReference("users").child(MainApplication.user.getUid()).child("podosensordata");
        dB.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int compteur = 0;
                float somme = 0;
                int i;

                for(i = 0; i < 7; i++)
                {
                    yValues.add(new Entry(i,0));
                }

                //graphique
                // yValues.add(new Entry(0,60f));
                // yValues.add(new Entry(1,50f));
                // yValues.add(new Entry(2,40f));
                // yValues.add(new Entry(3,30f));
                // yValues.add(new Entry(4,10f));
                // yValues.add(new Entry(5,50f));
                // yValues.add(new Entry(6,60f));

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    // nb_pas_veille.append(Float.toString(dataSnapshot1.getValue(PodoSensorData.class).getpMove()) + "\n");
                    if (compteur == 0)
                        nb_pas_veille.setText(Float.toString(dataSnapshot1.getValue(PodoSensorData.class).getpMove()) + "\n");

                    //if (compteur < 7)
                    //{
                     //   yValues.add(new Entry(compteur,dataSnapshot1.getValue(PodoSensorData.class).getpMove()));

                    //}


                    yValues.set(6,new Entry(6,dataSnapshot1.getValue(PodoSensorData.class).getpMove()));

                    for(i=0;i<6;i++)
                        yValues.set(i,new Entry(i,yValues.get(i+1).getY()));
                    compteur ++;

                    somme = somme + dataSnapshot1.getValue(PodoSensorData.class).getpMove();
                }

                int moyenne = 0;

                if(compteur > 0)
                    moyenne =(int) somme/compteur;

                affichage_nbpas_moyenne.setText("      Nb pas en moyenne : "+Integer.toString(moyenne));

                //Graphique
                LineDataSet Set1 = new LineDataSet(yValues, "7 derniers jours (ordre chronologique)");
                Set1.setFillAlpha(110);

                ArrayList <ILineDataSet> DataSets = new ArrayList<>();
                DataSets.add(Set1);

                LineData data = new LineData(DataSets);
                mChart.setData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }
    */
}
