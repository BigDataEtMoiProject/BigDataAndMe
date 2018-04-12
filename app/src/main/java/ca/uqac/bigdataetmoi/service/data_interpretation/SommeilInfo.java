package ca.uqac.bigdataetmoi.service.data_interpretation;

import android.media.MediaRecorder;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    private static final long MILLIS_ONE_DAY = 86400000;

    DatabaseManager dbManager;
    private int sleepTime; //temps de sommeil sur 24H en minutes

    public SommeilInfo () {
        dbManager = DatabaseManager.getInstance();
        sleepTime = 0;

        // S'il n'y a pas de données d'inscrite, on effectue le calcul et on inscrit la donnée

        // On trouve la date d'hier
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date hier = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
        String hierString = formatter.format(hier);

        // On vérifie si la donnée d'hier existe. Si elle n'existe pas, on fait le calcul.
        dbManager.getCalculationDbRef().orderByKey().equalTo(hierString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null || dataSnapshot.getChildren()==null) {
                    calcul();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    private void calcul() {

        // On trouve les timestamps qui limitent la journée précédente
        long hierTimestamp = System.currentTimeMillis() - MILLIS_ONE_DAY;
        long days = (hierTimestamp/MILLIS_ONE_DAY);
        long hierTimestampDebut = days * MILLIS_ONE_DAY;
        long hierTimestampFin = hierTimestampDebut + MILLIS_ONE_DAY-1;

        // Sélection des enregistrements de la journée précédente
        Query query = dbManager.getSensorDataDbRef().orderByKey()
                .startAt(Long.toString(hierTimestampDebut)).endAt(Long.toString(hierTimestampFin));
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // On boucle sur chaque enregistrement qui correspond à 5 minutes
                for(DataSnapshot child : dataSnapshot.getChildren() ){
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

    private Boolean isSleep(Boolean isMoving, Float lightLuxLevel, Float proximityDistance, Double soundLevel) {
        if(!isMoving && lightLuxLevel <= 10 && proximityDistance >= 15 && soundLevel <= 10)
            return true;
        else
            return false;
    }
}
