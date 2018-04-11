package ca.uqac.bigdataetmoi.service.data_interpretation;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.service.info_provider.InfoProvider;

/**
 * Created by Leonie on 20/03/2018.
 * Modified by Sebastien on 04/04/2018.
 * Interprétation des données du niveau sonore, de l'accéléromètre (en mouvement ou non), du capteur de proximité,
 * du capteur de lumière et de l'ouverure/fermeture de l'écran afin d'approximer le temps de sommeil
 */

public class SommeilInfo extends InfoProvider {

    DatabaseManager dbManager;
    private int sleepTime; //temps de sommeil sur 24H en minutes
    private Handler myHandler;
    private boolean verifSommeil; //verification si l'utilisateur dort ou non

    public int getSleepTime() {
        return sleepTime;
    }

    private Boolean isSleep(Boolean isMoving, Float lightLuxLevel, Float proximityDistance, Double soundLevel) {
        if(!isMoving && lightLuxLevel <= 10 && proximityDistance >= 15 && soundLevel <= 10)
            return true;
        else
            return false;
    }

    public SommeilInfo () {
        dbManager = DatabaseManager.getInstance();
        myHandler = new Handler();
        Log.i("APPLI", "SLEEP TIME IN A HOUR = " + sleepTime);
        myHandler.postDelayed(new Runnable() { //calcul du temps de sommeil sur les 10 dernieres minutes
            @Override
            public void run() {
                Log.i("APPLI", "VERIFICATION SI L'UTILISATEUR DORT SUR 10 MINUTES");
                calcul();
            }
        },180000); // execution de la fonction toutes les 10 min

        myHandler.postDelayed(new Runnable() {  //recuperation du temps de sommeil sur 24H
            @Override
            public void run() {
                Log.i("APPLI", "MOYENNE DU SOMMEIL SUR 24H");
                recup();
            }
        },86400000);    // execution de la fonction toutes les 24 heures
    }

    private void calcul() {
        //Récupérer les données de la bd (dernières 10 min)
        Query query = dbManager.getSensorDataDbRef().orderByKey().limitToLast(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // On retrouve la donnée
                if (dataSnapshot.hasChildren()) {
                    DataSnapshot child = dataSnapshot.getChildren().iterator().next();
                    DataCollection data = child.getValue(DataCollection.class);
                    if (!isSleep(data.isMoving, data.luxLevel, data.proximityDistance, data.soundLevel)) {
                        verifSommeil = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (verifSommeil) sleepTime = sleepTime + 10;   // on rajoute 10 min de sommeil à la journée}
            }
        });


    }

    private void recup() {
        //stockage dans la bd du temps de sommeil
        DataCollection collection = new DataCollection();
        //collection.sleepTime = getSleepTime();
        generateDataReadyEvent(collection);

        //reinitialisation de sleepTime
        sleepTime = 0;
    }
}
