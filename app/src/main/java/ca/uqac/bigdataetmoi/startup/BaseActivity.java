package ca.uqac.bigdataetmoi.startup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ca.uqac.bigdataetmoi.utility.PermissionManager;

/**
 * Created by Patrick Lapointe on 2018-02-14.
 *
 * But : Faciliter la gestion des permissions avec cette classe mère
 * qui permettera le stockage de l'activitée en cours
 * ainsi que l'appel de onRequestPermissionsResult pour chaque activitée.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityFetcherActivity.setCurrentActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
