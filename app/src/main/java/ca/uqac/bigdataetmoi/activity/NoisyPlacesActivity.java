package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import ca.uqac.bigdataetmoi.R;

public class NoisyPlacesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noisy_places);
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
