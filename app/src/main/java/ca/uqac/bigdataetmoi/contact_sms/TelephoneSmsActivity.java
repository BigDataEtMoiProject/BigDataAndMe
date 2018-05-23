package ca.uqac.bigdataetmoi.contact_sms;


import android.app.FragmentTransaction;
import android.os.Bundle;

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

        TelephoneSmsFragment frag = (TelephoneSmsFragment) getFragmentManager().findFragmentById(R.layout.fragment_telephone_sms);
        if (frag == null) {
            frag = new TelephoneSmsFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.telephone_sms_frame, frag);
            transaction.commit();
        }

        telephoneSmsPresenter = new TelephoneSmsPresenter(frag);
    }
}
