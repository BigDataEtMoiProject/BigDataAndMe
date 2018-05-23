package ca.uqac.bigdataetmoi.permission_manager;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.startup.BaseActivity;


public class PermissionActivity extends BaseActivity {
    private PermissionPresenter permissionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_manager);

        // On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);
        
        PermissionFragment frag = (PermissionFragment) getFragmentManager().findFragmentById(R.layout.fragment_permission_manager);
        if (frag == null) {
            frag = new PermissionFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.permission_frame, frag);
            transaction.commit();
        }

        permissionPresenter = new PermissionPresenter(frag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionPresenter.updateViewSwitches();
    }

}
