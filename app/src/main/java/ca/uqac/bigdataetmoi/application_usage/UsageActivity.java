package ca.uqac.bigdataetmoi.application_usage;

import android.app.FragmentTransaction;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.application_usage_details.ApplicationUsageDetailsActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;
import ca.uqac.bigdataetmoi.startup.BaseActivity;
import ca.uqac.bigdataetmoi.startup.MainMenuActivity;

/**
 * Created by Joshua on 18/04/2018
 * Affichage des données d'utilisation des applications
 */

@RequiresApi(22)
public class UsageActivity extends BaseActivity{

    private UsageStatsManager statsManager;
    private UsagePresenter up;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);
        setupActionBar();

        mDrawerLayout = findViewById(R.id.drawer_layout2);

        UsageFragment frag = (UsageFragment) getFragmentManager().findFragmentById(R.layout.fragment_donnees_utilisation);
        if (frag == null) {
            frag = new UsageFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.donnees_utilisation_frame, frag);
            transaction.commit();
        }
        up = new UsagePresenter(frag);

        BigDataService.startRecurrence(getApplicationContext());
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Back arrow clicked
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void click(View view){
        startActivity(new Intent(this, ApplicationUsageDetailsActivity.class));
    }
}

