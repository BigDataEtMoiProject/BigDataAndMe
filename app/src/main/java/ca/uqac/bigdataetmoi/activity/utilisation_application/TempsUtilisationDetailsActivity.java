package ca.uqac.bigdataetmoi.activity.utilisation_application;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.BaseActivity;

public class TempsUtilisationDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_temps_utilisation_details);
        setupActionBar();
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
}
