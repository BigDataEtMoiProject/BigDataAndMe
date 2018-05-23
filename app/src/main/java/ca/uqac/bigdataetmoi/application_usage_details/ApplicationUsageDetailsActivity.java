package ca.uqac.bigdataetmoi.application_usage_details;

        import android.app.FragmentTransaction;
        import android.app.usage.UsageStatsManager;
        import android.os.Bundle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBar;

        import ca.uqac.bigdataetmoi.R;
        import ca.uqac.bigdataetmoi.service.BigDataService;
        import ca.uqac.bigdataetmoi.startup.BaseActivity;

public class ApplicationUsageDetailsActivity extends BaseActivity {

    private UsageStatsManager statsManager;
    private ApplicationUsageDetailsPresenter up;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temps_utilisation_details);

        mDrawerLayout = findViewById(R.id.drawer_layout3);

        ApplicationUsageDetailsFragment frag = (ApplicationUsageDetailsFragment) getFragmentManager().findFragmentById(R.layout.fragment_temps_utilisation_details);
        if (frag == null) {
            frag = new ApplicationUsageDetailsFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.donnees_utilisation_details_frame, frag);
            transaction.commit();
        }
        up = new ApplicationUsageDetailsPresenter(frag);
    }

}