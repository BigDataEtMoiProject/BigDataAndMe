package ca.uqac.bigdataetmoi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.util.ArrayList;

import ca.uqac.bigdataetmoi.MainApplication;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.PodoSensorData;
import ca.uqac.bigdataetmoi.service.MyService;

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

        Intent serviceIntent = new Intent(CompteurDePasActivity.this
                , MyService.class);

        CompteurDePasActivity.this.startService(serviceIntent);

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

                    if (compteur < 7)
                    {
                        yValues.add(new Entry(compteur,dataSnapshot1.getValue(PodoSensorData.class).getpMove()));

                    }



                    compteur ++;

                    somme = somme + dataSnapshot1.getValue(PodoSensorData.class).getpMove();


                }
                int moyenne =(int) somme/compteur;

                affichage_nbpas_moyenne.setText(Integer.toString(moyenne));

                //Graphique
                LineDataSet Set1 = new LineDataSet(yValues, "Data Set 1");
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

                textView.setText("Nb de pas aujourd'hui" + Float.toString(nb) + "pas");

                handler.postDelayed(this, 1000);

            }
        });


    }


}










