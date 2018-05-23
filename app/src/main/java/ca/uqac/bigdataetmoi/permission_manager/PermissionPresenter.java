package ca.uqac.bigdataetmoi.permission_manager;

import android.support.annotation.NonNull;

import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECORD_AUDIO;

public class PermissionPresenter implements IPermissionContract.Presenter {

    private static final long MILLIS_ONE_DAY = 86400000;
    private IPermissionContract.View view;
    private PermissionManager permissionManager;

    public PermissionPresenter(@NonNull IPermissionContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {
        permissionManager = PermissionManager.getInstance();

        // On met à jour l'état des switch et ENSUITE on active les listeners
        updateViewSwitches();
        view.setListeners();
    }

    @Override
    public void setPermissionGranted(String permission, boolean granted) {
        permissionManager.setPermissionGranted(permission, granted);
    }

    @Override
    public void updateViewSwitches() {
        view.setLocationChecked(permissionManager.isGranted(ACCESS_FINE_LOCATION));
        view.setMicrophoneChecked(permissionManager.isGranted(RECORD_AUDIO));
        view.setSmsChecked(permissionManager.isGranted(READ_SMS));
        view.setContactsChecked(permissionManager.isGranted(READ_CONTACTS));
        view.setBluetoothChecked(permissionManager.isGranted(BLUETOOTH_ADMIN));
        view.setWifiChecked(permissionManager.isGranted(ACCESS_WIFI_STATE));
        view.setUsageChecked(permissionManager.isGranted(PACKAGE_USAGE_STATS));
    }
}
