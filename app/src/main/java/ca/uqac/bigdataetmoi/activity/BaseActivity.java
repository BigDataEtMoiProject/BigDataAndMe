package ca.uqac.bigdataetmoi.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import ca.uqac.bigdataetmoi.MainApplication;
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
    protected void onResume() {
        super.onResume();
        MainApplication.setCurrentActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
