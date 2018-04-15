package ca.uqac.bigdataetmoi.localisation;

import android.os.Bundle;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.startup.BaseActivity;

public class LocalisationActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.activity_localisation);

        ActivityFetcherActivity.setCurrentActivity(this);


    }
}
