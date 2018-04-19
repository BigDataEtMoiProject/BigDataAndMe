package ca.uqac.bigdataetmoi.application_usage;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;


import ca.uqac.bigdataetmoi.R;

/**
 * Created by Joshua on 18/04/2018
 * Affichage des données d'utilisation des applications
 */

@RequiresApi(21)
public class UsageActivity extends AppCompatActivity{private UsageStatsManager statsManager;
    private UsagePresenter up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);

        up = new UsagePresenter((UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE), getPackageManager());

    }

}

