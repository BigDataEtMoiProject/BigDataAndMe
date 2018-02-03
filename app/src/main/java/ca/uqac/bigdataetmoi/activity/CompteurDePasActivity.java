package ca.uqac.bigdataetmoi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.bigdataetmoi.MainApplication;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data_models.PodoSensorData;

//Dans cette classe on affiche le nombre de pas en temps réel , la moyenne du nombre de pas ainsi que le nombre de pas durant les 7 derniers jours
//Appuyer sur no chart data available pour afficher le graphique au debut

public class CompteurDePasActivity extends AppCompatActivity {


    TextView textView;
    public static float nb;
    TextView nb_pas_veille;
    TextView affichage_nbpas_moyenne;

    //graphique
    private LineChart mChart;
    ArrayList <Entry> yValues;



    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compteur_de_pas);

        textView = (TextView)findViewById(R.id.txt);

        //graphique
        mChart = (LineChart)findViewById(R.id.Linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        yValues = new ArrayList<>();



        //Ajouté
        db=FirebaseDatabase.getInstance();

        affichage_nbpas_moyenne = (TextView) findViewById(R.id.moyenne_pas);

        nb_pas_veille = (TextView) findViewById(R.id.nb_pas_veille);

        nb_pas_veille.setText("");
        affichage_nbpas_moyenne.setText("");
        RecupererValeur();




        runTimer();


    }



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
                    {
                        nb_pas_veille.setText(Float.toString(dataSnapshot1.getValue(PodoSensorData.class).getpMove()) + "\n");

                    }
                    /*
                    if (compteur < 7)
                    {
                        yValues.add(new Entry(compteur,dataSnapshot1.getValue(PodoSensorData.class).getpMove()));

                    }
                    */

                    yValues.set(6,new Entry(6,dataSnapshot1.getValue(PodoSensorData.class).getpMove()));

                    for(i=0;i<6;i++)
                    {
                        yValues.set(i,new Entry(i,yValues.get(i+1).getY()));

                    }
                    compteur ++;

                    somme = somme + dataSnapshot1.getValue(PodoSensorData.class).getpMove();


                }
                int moyenne =(int) somme/compteur;

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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runTimer() {

        //java text view associated with the xml one
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
 /*
                if(nb == 0)
                {

                    RecupererValeur();
                }
*/
                textView.setText("      COMPTEUR DE PAS\n\n      Nb de pas aujourd'hui : " + Float.toString(nb) + "pas");

                handler.postDelayed(this, 1000);

            }
        });


    }


}



















