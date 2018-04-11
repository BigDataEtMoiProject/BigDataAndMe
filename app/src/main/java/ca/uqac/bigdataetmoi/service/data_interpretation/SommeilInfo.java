package ca.uqac.bigdataetmoi.service.data_interpretation;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

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
        sleepTime = 0;

        // On trouve la date d'hier
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date hier = cal.getTime();
        Log.i("Hier : ", hier.toString());

        // TODO On vérifie si la donnée de la journée précédente est inscrite dans la BD

        // S'il n'y a pas de données d'inscrite, on effectue le calcul et on inscrit la donnée
        calcul();
    }

    private void calcul() {
        //Récupérer les données de la bd (dernières 10 min)
        Query query = dbManager.getSensorDataDbRef().orderByKey().limitToLast(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // On retrouve la donnée
                while (dataSnapshot.hasChildren()) {
                    DataSnapshot child = dataSnapshot.getChildren().iterator().next();
                    DataCollection data = child.getValue(DataCollection.class);
                    if (isSleep(data.isMoving, data.luxLevel, data.proximityDistance, data.soundLevel)) {
                        sleepTime += 5;
                    }
                }
                dbManager.storeSommeilCalculationData(sleepTime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
