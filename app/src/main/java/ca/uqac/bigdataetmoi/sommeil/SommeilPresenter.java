package ca.uqac.bigdataetmoi.sommeil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.SommeilData;
import ca.uqac.bigdataetmoi.database.helper.SommeilDataHelper;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class SommeilPresenter implements ISommeilContract.Presenter, DataReadyListener {

    private static final long MILLIS_ONE_DAY = 86400000;
    private ISommeilContract.View view;

    public SommeilPresenter(@NonNull ISommeilContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {
        Range unMois = getTimestampRangeFromNow(30);
        fetchSommeilInfo(unMois.lower, unMois.upper);
    }

    @Override
    public void fetchSommeilInfo(long debut, long fin) {
        // On effectue la requête pour récupérer les données de un mois
        SommeilDataHelper sommeilDataHelper = new SommeilDataHelper();
        sommeilDataHelper.fetchAllSommeilData(this, debut, fin);
    }

    @Override
    public void dataReady(Object o) {

        List<SommeilData> sommeilInfos = new ArrayList<SommeilData>();
        List<Long> timestamps = new ArrayList<Long>();

        if(o.getClass() == DataSnapshot.class) {
            DataSnapshot data = (DataSnapshot) o;

            if(!data.hasChildren())
                return;

            for(DataSnapshot child : data.getChildren()) {
                timestamps.add(Long.parseLong(child.getKey()));
                sommeilInfos.add(child.getValue(SommeilData.class));
            }

            if(sommeilInfos.size() > 0) {
                calculateSommeilForDisplay(timestamps, sommeilInfos);
            }

        } else if (o.getClass() == Exception.class) {
            Log.d("BDEM_ERROR", ((Exception)o).getMessage());
        }
    }

    private Range getTimestampRangeFromNow(int daysInRange) {
        long timestamp = System.currentTimeMillis() - daysInRange * MILLIS_ONE_DAY;
        long days = (timestamp/MILLIS_ONE_DAY);
        long debutMillis = days * MILLIS_ONE_DAY;
        long finMillis = debutMillis + daysInRange * MILLIS_ONE_DAY - 1;

        return new Range(debutMillis, finMillis);
    }

    private void calculateSommeilForDisplay(List<Long> timestamps, List<SommeilData> sommeilInfos) {

        // Trouver les ranges concernées
        Range unMoisRange = getTimestampRangeFromNow(30);
        Range uneSemianeRange = getTimestampRangeFromNow(7);
        Range unJourRange = getTimestampRangeFromNow(1);

        // On parcours le résultat afin de calculer la moyenne de l'interval de un mois et de une semaine.
        // on fait aussi la somme de la journée d'avant
        long totalMois = 0;
        long nbEntreesMois = 0;
        long totalSemaine = 0;
        long nbEntreesSemaine = 0;
        long totalHier = 0;

        for (int i = 0 ; i < sommeilInfos.size() ; i++)
        {
            long timestamp = timestamps.get(i);
            SommeilData data = sommeilInfos.get(i);

            if(timestamp <= unJourRange.upper && data.getIsSleeping())
            {
                if(timestamp >= unJourRange.lower) {
                    totalHier += 5;
                }
                if(timestamp >= uneSemianeRange.lower) {
                    totalSemaine += 5;
                    nbEntreesSemaine++;
                }
                if(timestamp >= unMoisRange.lower) {
                    totalMois += 5;
                    nbEntreesMois++;
                }
            }
        }

        // Affichage des résultats
        double moyenneMois = 0;
        if(nbEntreesMois > 0)
            moyenneMois = (totalMois / nbEntreesMois) / 60.0;

        double moyenneSemaine = 0;
        if(nbEntreesSemaine > 0)
            moyenneSemaine = (totalSemaine / nbEntreesSemaine) / 60.0;

        view.afficherMoyenneMoisDernier(moyenneMois);
        view.afficherMoyenneSemaineDern(moyenneSemaine);
        view.afficherTempsHier(totalHier / 60.0);
    }


    // Classe pour donner un retour à la fonction getTimestampRangeFromNow
    private class Range {
        public long lower, upper;

        Range(long lower, long upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }
}
