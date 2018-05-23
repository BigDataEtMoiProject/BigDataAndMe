package ca.uqac.bigdataetmoi.sommeil;

import android.app.FragmentTransaction;
import android.os.Bundle;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.startup.BaseActivity;


public class SommeilActivity extends BaseActivity {
    private SommeilPresenter sommeilPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sommeil);

        // On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);
        
        SommeilFragment frag = (SommeilFragment) getFragmentManager().findFragmentById(R.layout.fragment_sommeil);
        if (frag == null) {
            frag = new SommeilFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.sommeil_frame, frag);
            transaction.commit();
        }

        sommeilPresenter = new SommeilPresenter(frag);
    }
}
