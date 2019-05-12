package ca.uqac.bigdataetmoi.contact_sms;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.startup.BaseActivity;

public class TelephoneSmsActivity extends BaseActivity {
    private TelephoneSmsPresenter telephoneSmsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_sms);

        // On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);


    }
}
